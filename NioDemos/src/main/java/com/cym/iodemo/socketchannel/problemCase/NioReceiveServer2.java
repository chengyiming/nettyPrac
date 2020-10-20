package com.cym.iodemo.socketchannel.problemCase;

import com.cym.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class NioReceiveServer2 {

    private Charset charset = Charset.forName("UTF-8");

    private ByteBuffer serverBuffer = ByteBuffer.allocate(10240);

    public void startServer() throws IOException
    {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        //绑定连接，进行监听
        serverSocketChannel.socket().bind(new InetSocketAddress(
                16668
        ));

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        Logger.info("哈哈哈哈");


        while (selector.select() > 0)
        {
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext())
            {
                SelectionKey key = it.next();
                if (key.isAcceptable())
                {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    if (socketChannel == null) continue;
                    socketChannel.configureBlocking(false);
                    SelectionKey selectionKey =
                            socketChannel.register(selector, SelectionKey.OP_READ);
                    Logger.debug(socketChannel.getRemoteAddress() + "连接成功...");

                } else if (key.isReadable())
                {
                    processData(key);
                }
                it.remove();
            }
        }
    }



    /**
     * 处理客户端传输过来的数据
     */
    private void processData(SelectionKey key) throws IOException
    {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int num = 0;
        try
        {
            serverBuffer.clear();
            while ((num = socketChannel.read(serverBuffer)) > 0)
            {
                System.out.println(num);
                serverBuffer.flip();
                int fileNameLen = serverBuffer.getInt();
                byte[] fileNameBytes = new byte[fileNameLen];
                serverBuffer.get(fileNameBytes);

                String fileName = new String(fileNameBytes, charset);
                Logger.info("NIO  传输目标文件name：", fileName);
                serverBuffer.clear();
            }
            key.cancel();
        } catch (IOException e)
        {
            key.cancel();
            e.printStackTrace();
            return;
        }
    }

    public static void main(String[] args) throws IOException {
        new NioReceiveServer2().startServer();
    }

}
