package com.cym.netty.simple.reactor.echo;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * 回显的客户端
 */
public class EchoClientReactor{
    String HOSTHOME = "localhost";
    int PORT = 6668;


    public void start() throws Exception{
        InetSocketAddress address = new InetSocketAddress(HOSTHOME, PORT);
        //获得通道
        SocketChannel socketChannel = SocketChannel.open(address);
        socketChannel.configureBlocking(false);

        while(!socketChannel.finishConnect()) {}
        System.out.println("客户端连接成功~");
        new Thread(new Processor(socketChannel)).start();
    }

    static class Processor implements Runnable {
        final Selector selector;
        final SocketChannel socketChannel;

        Processor(SocketChannel socketChannel) throws IOException {
            this.socketChannel = socketChannel;
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_WRITE|SelectionKey.OP_READ);

        }
        @Override
        public void run() {
            try {
                while(!Thread.interrupted()) {
                    selector.select();
                    //TODO 一个客户端只有一个channel吧，好像也不是，一个客户端可以连接多个服务器
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while(iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isWritable()) {
                            //TODO
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            Scanner scanner = new Scanner(System.in);
                            System.out.println("请输入发送内容：");
                            if(scanner.hasNext()) {
                                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                                String content = scanner.next();
//                                int length = 0;
                                buffer.put(content.getBytes());
                                buffer.flip();
                                //TODO
//                                if((length = socketChannel.write(buffer1)) != -1) {
//                                    System.out.println("to server " + new String(buffer1.array()));
//                                    buffer1.clear();
//                                }
                                socketChannel.write(buffer);
                                buffer.clear();
                            }
                        }

                        if(selectionKey.isReadable()) {
                            SocketChannel channel = (SocketChannel)selectionKey.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int length = 0;
                            //TODO
                            while((length = channel.read(buffer))> 0) {
                                //读buffer
                                buffer.flip();
                                System.out.println("server echo:" + new String(buffer.array(), 0, length));
                                buffer.clear();
                            }

                        }

                    }
                    //这里为什么需要清空，不明白
                    selectionKeys.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception{
        new EchoClientReactor().start();
    }

}
