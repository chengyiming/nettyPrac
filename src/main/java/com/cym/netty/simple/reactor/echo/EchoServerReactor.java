package com.cym.netty.simple.reactor.echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoServerReactor implements Runnable {
    Selector selector;
    ServerSocketChannel serverSocketChannel;
    EchoServerReactor() throws Exception{
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        //监听这个端口，查看有没有连接
        serverSocketChannel.socket().bind(new InetSocketAddress("localhost",6668));
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new AcceptorHandler());
    }

    public void run() {
        try {
            while(!Thread.interrupted()) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    dispatch(sk);
                }
                //TODO 这里为什么要写这个，不写会发生什么，感觉不写也可以
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void dispatch(SelectionKey sk) {
        Runnable handler =  (Runnable) sk.attachment();
        if(handler != null) {
            handler.run();
        }
    }

    class AcceptorHandler implements Runnable {
        public void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                //设置为非阻塞
                if (socketChannel != null) {
                    new EchoHandler(selector, socketChannel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Thread(new EchoServerReactor()).start();
    }
}
