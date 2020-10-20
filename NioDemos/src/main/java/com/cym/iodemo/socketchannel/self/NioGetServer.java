package com.cym.iodemo.socketchannel.self;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 对应的服务端
 * 接收并进行存储
 */
public class NioGetServer implements Runnable{
    Selector selector;
    ServerSocketChannel serverSocketChannel;
    final String HOSTHOME = "localhost";
    final int PORT = 6666;
    @Override
    public void run() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(
                    new InetSocketAddress(HOSTHOME, PORT)
            );
            serverSocketChannel.configureBlocking(false);//配置非阻塞模式
            //注册感兴趣的事件
            SelectionKey serverSocketSk = serverSocketChannel.register(selector, 0);
            serverSocketSk.attach(new AcceptHandler());
            serverSocketSk.interestOps(SelectionKey.OP_ACCEPT);

            while(true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    Runnable handler = (Runnable) selectionKey.attachment();
                    handler.run();
                    // TODO 这里一定要删除，或者是写下面的部分，selectionKeys.clear()，
                    // NIO的特点只会累加，已选择的键的集合不会删除
                    // 如果不删除，下一次又会被select函数选中
                    iterator.remove();
                }
//                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class AcceptHandler implements Runnable {

        @Override
        public void run() {
            try {
                //这里得到的结果是空
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
                selectionKey.attach(new DataHandler(socketChannel));
                System.out.println("连接完毕~~");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class DataHandler implements Runnable {
        private SocketChannel socketChannel;
        DataHandler(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }
        //读取数据，存入磁盘
        @Override
        public void run() {
            try {
                System.out.println("开始写~~");
                FileOutputStream fileOutputStream = new FileOutputStream("to_socket.jpg");
                FileChannel outputStreamChannel = fileOutputStream.getChannel();
                //本来想使用transferFrom方式进行传输
                //但是，不能事先知道文件的大小，因此放弃使用
                int length = 0;
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                while((length = socketChannel.read(buffer)) != -1) {
                    buffer.flip();
                    while((length = outputStreamChannel.write(buffer)) != 0){}
                    buffer.clear();
                }
                outputStreamChannel.force(true);//刷新
                outputStreamChannel.close();
                socketChannel.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new NioGetServer()).start();
    }
}
