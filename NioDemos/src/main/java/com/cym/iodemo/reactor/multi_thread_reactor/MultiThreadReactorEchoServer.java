package com.cym.iodemo.reactor.multi_thread_reactor;

import com.cym.NioDemoConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cym
 * @Date :2020-10-19
 */
public class MultiThreadReactorEchoServer {

    private  ServerSocketChannel serverSocketChannel;
    private  Selector[] selectorArray = new Selector[2];

    private AtomicInteger atomicInteger =
            new AtomicInteger(0);

    private MultiThreadReactorEchoServer(){
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            for(int i = 0; i < 2; i++) {
                selectorArray[i] = Selector.open();
            }
            SelectionKey selectionKey = serverSocketChannel.register(selectorArray[0], SelectionKey.OP_ACCEPT);
            selectionKey.attach(new AcceptHandler());
            serverSocketChannel.socket().bind(new InetSocketAddress(
                    NioDemoConfig.SOCKET_SERVER_IP,
                    NioDemoConfig.SOCKET_SERVER_PORT ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startServer() {
        new Thread(new SubReactor(selectorArray[0])).start();
        new Thread(new SubReactor(selectorArray[1])).start();

    }

    class SubReactor implements Runnable{
        Selector selector;

        SubReactor(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+" start.....");
            int i = 0;
            try {
                while(selector.select()> 0) {
                    i++;
                    System.out.println("do:"+i);
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    System.out.println("selectionKeys.size():" + selectionKeys.size());
                    while(iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        System.out.println("读就绪"+selectionKey.isReadable());
                        System.out.println("写就绪"+selectionKey.isWritable());
                        System.out.println("连接就绪"+selectionKey.isAcceptable());
                        dispatch(selectionKey);
                        iterator.remove();
                        System.out.println("iter:"+i);
                    }
                    System.out.println("selectionKeys.size():" + selectionKeys.size());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private void dispatch(SelectionKey selectionKey) {
            Runnable runnable = (Runnable) selectionKey.attachment();
            if(runnable != null) {
                runnable.run();
            }
        }


    }
    class AcceptHandler implements Runnable {
        @Override
        public void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                System.out.println(Thread.currentThread().getName()+"connect...");
                if(socketChannel != null) {
                    new ReadOrWriteHandler(selectorArray[atomicInteger.getAndIncrement()%selectorArray.length],
                            socketChannel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        MultiThreadReactorEchoServer multiServerReactor = new MultiThreadReactorEchoServer();
        multiServerReactor.startServer();
    }

}

