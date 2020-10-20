package com.cym.iodemo.socketchannel.standard;

import com.cym.NioDemoConfig;
import com.cym.util.IOUtil;
import com.cym.util.Logger;
import com.cym.util.Print;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NioReceiveServer {
    //接收文件的路径
    private static final String RECEIVE_PATH = NioDemoConfig.SOCKET_RECEIVE_PATH;
    private Charset charset = Charset.forName("UTF-8");

    // todo 静态内部类有什么特别的吗？？
    /**
     * 服务点保存的客户端对象，对应一个客户端文件
     */
    static class Client {
        //文件名称
        String fileName;
        //文件长度
        long fileLength;
        //开始时间
        long startTime;
        //客户端的地址
        InetSocketAddress remoteAddress;

        //接受的文件长度
        long receiveLength;

        //输出的通道
        FileChannel outChannel;

        public boolean isFinished() {
            return receiveLength >= fileLength;
        }
    }

    //服务端这边使用的缓存的大小
    private ByteBuffer serverBuffer = ByteBuffer.allocate(NioDemoConfig.SERVER_BUFFER_SIZE);

    //使用map保存每个哭护短传输，当可读时间发生的时候，根据channel找到对应的对象
    //todo 不需要这样，我们可以通过socketchannel找到对应的客户端
    Map<SelectableChannel, Client> clientMap = new HashMap<>();


    public void startServer() throws IOException
    {
        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        SelectionKey serverSk = serverSocketChannel.register(selector, 0);
        serverSk.interestOps(SelectionKey.OP_ACCEPT);


        //绑定连接，进行监听
        serverSocketChannel.socket().bind(new InetSocketAddress(
                NioDemoConfig.SOCKET_SERVER_PORT
        ));
        int count = 0;
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        Logger.info("serverChannel is linstening...");
        // 6、轮询感兴趣的I/O就绪事件（选择键集合）
        while (selector.select() > 0)
        {
            System.out.println("count:"+count++);
            // 7、获取选择键集合
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext())
            {
                // 8、获取单个的选择键，并处理
                SelectionKey key = it.next();

                // 9、判断key是具体的什么事件，是否为新连接事件
                if (key.isAcceptable())
                {
                    // 10、若接受的事件是“新连接”事件,就获取客户端新连接
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    if (socketChannel == null) continue;
                    // 11、客户端新连接，切换为非阻塞模式
                    socketChannel.configureBlocking(false);
                    // 12、将客户端新连接通道注册到selector选择器上
                    SelectionKey selectionKey =
                            socketChannel.register(selector, SelectionKey.OP_READ);
                    // 余下为业务处理
                    Client client = new Client();
                    client.remoteAddress
                            = (InetSocketAddress) socketChannel.getRemoteAddress();
                    clientMap.put(socketChannel, client);
                    Logger.debug(socketChannel.getRemoteAddress() + "连接成功...");

                } else if (key.isReadable())
                {
                    processData(key);
                }
                // NIO的特点只会累加，已选择的键的集合不会删除
                // 如果不删除，下一次又会被select函数选中
                it.remove();
            }
        }
    }


    /*public void startServer() {
        try {
            Selector selector = Selector.open();

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            SelectionKey serverSk = serverSocketChannel.register(selector, 0);
            serverSk.interestOps(SelectionKey.OP_ACCEPT);


            //绑定连接，进行监听
            serverSocketChannel.socket().bind(new InetSocketAddress(
                     NioDemoConfig.SOCKET_SERVER_PORT
            ));

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            Logger.info("服务端开始进行了监听......");
            // todo

        //其实，我总觉得selector.select()和selector.selectedKeys的方法作用是一样的，
        //前面这个没必要

            while(selector.select() > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isAcceptable()) {
                        // todo 这里和答案写得不太一样，但是我觉得从头到尾都是只有一个serverSocketChannel
                        ServerSocketChannel server = (ServerSocketChannel)selectionKey.channel();
                        SocketChannel socketChannel = server.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        //这里构建client对象，放入Map
                        //下面得表示业务处理
                        Client client = new Client();
                        client.remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
                        //todo 客户端和服务端得socketChannel是不一样得，运行在不同的工程下面
                        clientMap.put(socketChannel, client);
                        Logger.debug(socketChannel.getRemoteAddress() + "连接成功...");
                    } else if(selectionKey.isReadable()) {
                        processData(selectionKey);
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

    /**
     * 处理客户端传递的数据
     * @param key
     */
    /*public void processData(SelectionKey key) {
        Client client = clientMap.get(key.channel());
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int length = 0;
        try {
            serverBuffer.clear();
            while((length = socketChannel.read(serverBuffer)) != -1) {
                serverBuffer.flip();
                if(StringUtils.isBlank(client.fileName)) {
                    //因为是int类型，需要占据四个字节
                    int fileNameLen = serverBuffer.getInt();
                    byte[] fileNameByte = new byte[fileNameLen];
                    serverBuffer.get(fileNameByte);
                    //文件名称
                    String fileName = new String(fileNameByte, charset);
                    client.fileName = fileName;
                    //todo 之前用的一直文件名称，这次是一个目录
                    File destdir = new File(RECEIVE_PATH);
                    if (!destdir.exists()) {
                        //创建的是一个目录
                        destdir.mkdir();
                    }
                    Logger.info("Nio 传输目标dir:" + destdir);
                    String fullName = destdir.getAbsolutePath() + File.separatorChar + client.fileName;
                    Logger.info("Nio 传输的目标文件是：" +fullName);
                    File file = new File(fullName.trim());
                    if(!file.exists()) {
                        file.createNewFile();
                    }
                    FileChannel outputChannel = new FileOutputStream(file).getChannel();
                    client.outChannel = outputChannel;
                    long fileLength = serverBuffer.getLong();
                    client.fileLength  = fileLength;
                    client.startTime = System.currentTimeMillis();
                    Logger.info("传输开始了...");
                    client.receiveLength += serverBuffer.capacity();
                    if(serverBuffer.capacity() > 0) {
                        client.outChannel.write(serverBuffer);
                    }
                    if(client.isFinished()) {
                        finished(key, client);
                    }
                }
                serverBuffer.clear();
            }
            //todo 这里为什么需要cancel,已经有了remove为什么还需要cancel
            key.cancel();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }*/

    /**
     * 处理客户端传输过来的数据
     */
    private void processData(SelectionKey key) throws IOException
    {
        Client client = clientMap.get(key.channel());

        SocketChannel socketChannel = (SocketChannel) key.channel();
        int num = 0;
        try
        {
            serverBuffer.clear();

            /*while((num = socketChannel.read(serverBuffer)) > 0){
                Thread.sleep(1000);
            }*/
            while((num = socketChannel.read(serverBuffer)) > 0) {
                System.out.println("num=" + num);
                if(serverBuffer.position() < 550) {
                    continue;
                }
                serverBuffer.flip();
                //客户端发送过来的，首先处理文件名
                if (null == client.fileName) {
                    int fileNameLen = serverBuffer.getInt();
                    System.out.println("fileNameLen=" + fileNameLen);
                    byte[] fileNameBytes = new byte[fileNameLen];
                    serverBuffer.get(fileNameBytes);

                    // 文件名
                    String fileName = new String(fileNameBytes, charset);

                    File directory = new File(RECEIVE_PATH);
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    Logger.info("NIO  传输目标dir：", directory);

                    client.fileName = fileName;
                    String fullName = directory.getAbsolutePath() + File.separatorChar + fileName;
                    Logger.info("NIO  传输目标文件：", fullName);

                    File file = new File(fullName.trim());

                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileChannel fileChannel = new FileOutputStream(file).getChannel();
                    client.outChannel = fileChannel;

/*                    if (serverBuffer.capacity() < 8)
                {
                    continue;
                }*/
                    // 文件长度
                    long fileLength = serverBuffer.getLong();
                    client.fileLength = fileLength;
                    client.startTime = System.currentTimeMillis();
                    Logger.debug("NIO  传输开始：");

                    client.receiveLength += serverBuffer.capacity();
                    if (serverBuffer.capacity() > 0) {
                        // 写入文件
                        client.outChannel.write(serverBuffer);
                    }
                    if (client.isFinished()) {
                        finished(key, client);
                    }
                }
                //客户端发送过来的，最后是文件内容
                else {
                    client.receiveLength += serverBuffer.capacity();
                    // 写入文件
                    client.outChannel.write(serverBuffer);
                    if (client.isFinished()) {
                        finished(key, client);
                    }
                }
                serverBuffer.clear();
            }
            System.out.println("出来的num:"+num);

        //key.cancel();
        } catch (IOException e)
        {
            key.cancel();
            e.printStackTrace();
            return;
        }
        // 调用close为-1 到达末尾
        if (num == -1)
        {
            finished(key, client);
        }
    }

    private void finished(SelectionKey key, Client client) {
        IOUtil.closeQuietly(client.outChannel);
        Logger.info("上传完毕");
        key.cancel();
        Logger.debug("文件接受成功，FileName :" + client.fileName);
        Logger.debug("Size:" + IOUtil.getFormatFileSize(client.fileLength));
        long endTime = System.currentTimeMillis();
        Logger.debug("ＮＩＯ IO传输的毫秒数：" + (endTime - client.startTime));
    }

    public static void main(String[] args) throws IOException {
        new NioReceiveServer().startServer();
    }

}
