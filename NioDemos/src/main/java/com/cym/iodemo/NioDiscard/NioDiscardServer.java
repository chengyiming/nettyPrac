package com.cym.iodemo.NioDiscard;




import com.cym.NioDemoConfig;
import com.cym.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 服务端
 */
public class NioDiscardServer {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;


    NioDiscardServer() throws IOException {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
    }


    private void startServer() {
        try {
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(
                    new InetSocketAddress(NioDemoConfig.SOCKET_SERVER_IP,
                            NioDemoConfig.SOCKET_SERVER_PORT));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while(selector.select() > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                ByteBuffer buffer = ByteBuffer.allocate(NioDemoConfig.SERVER_BUFFER_SIZE);
                while(iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    //处理连接请求
                    if(selectionKey.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if(selectionKey.isReadable()) {
                        //开始读事件
                        SocketChannel readChannel = (SocketChannel)selectionKey.channel();
                        int length = 0;
                        while((length = readChannel.read(buffer)) != 0) {
                            //System.out.println("buffer.capacity():"+buffer.capacity());
                            Logger.info("得到的信息是：" + new String(buffer.array())+"丢弃之");
                        }
                        //关闭连接 todo 然后再发送数据会发生什么呢
                        //readChannel.close();
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("出现了异常");
        }
    }

    public static void main(String[] args) throws IOException {
        new NioDiscardServer().startServer();
    }
}
