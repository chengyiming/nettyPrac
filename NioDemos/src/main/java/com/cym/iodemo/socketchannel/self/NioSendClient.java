package com.cym.iodemo.socketchannel.self;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author cym
 * @Date 2020-10-12
 * 使用filejChannel文件通道读取本地文件的内容
 * 在客户端使用socketchannel套接字通道，
 * 把文件信息和文件内容发送给服务器
 */
public class NioSendClient implements Runnable{
    Selector selector;
    SocketChannel socketChannel;
    final String HOSTHOME = "localhost";
    final int PORT = 6666;

    public static void main(String[] args) {
        new Thread(new NioSendClient()).start();
    }

    @Override
    public void run() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);//read, connect, write直接返回
            socketChannel.connect(new InetSocketAddress(HOSTHOME, PORT));
            while(!socketChannel.finishConnect()) {}
            System.out.println("客户端连接成功~~~");
            //--------------准备发送数据
            File file = new File("from.jpg");
            FileInputStream fileInputStream = new FileInputStream(file);
            FileChannel inputStreamChannel = fileInputStream.getChannel();
            int length = 0;
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while((length = inputStreamChannel.read(buffer)) != -1) {
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
            }
            inputStreamChannel.close();
            socketChannel.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
