package com.cym.iodemo.socketchannel.standard;

import com.cym.NioDemoConfig;
import com.cym.util.IOUtil;
import com.cym.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * 这一部分是抄的书籍
 * 文件传输client端
 */
public class NioSendClient {
    private Charset charset = Charset.forName("utf-8");


    /**
     * 向服务器传输文件
     */
/*    public void sendFile() {
        try {
            String sourcePath = NioDemoConfig.SOCKET_SEND_FILE;
            String srcPath = IOUtil.getResourcePath(sourcePath);
            Logger.debug("srcPath=" + srcPath);

            String destFile = NioDemoConfig.SOCKET_RECEIVE_FILE;
            Logger.debug("destFile=" + destFile);

            File file = new File(srcPath);
            if(!file.exists()) {
                Logger.debug("文件不存在");
                return;
            }

            FileInputStream fileInputStream = new FileInputStream(file);
            FileChannel inputChannel = fileInputStream.getChannel();
            SocketChannel socketChannel = SocketChannel.open();
            //todo 这里总是出现这个错误，客户端是不需要进行非阻塞设置
            //socketChannel.configureBlocking(false);
            socketChannel.socket().connect(
                    new InetSocketAddress(NioDemoConfig.SOCKET_SERVER_IP,
                            NioDemoConfig.SOCKET_SERVER_PORT)
            );

            while(!socketChannel.finishConnect()) {}
            Logger.debug("Client成功连接服务器");


            ByteBuffer fileNamebuffer = charset.encode(destFile);
            ByteBuffer buffer = ByteBuffer.allocate(NioDemoConfig.SEND_BUFFER_SIZE);

            //发送文件名称的长度
            int fileNameLen = fileNamebuffer.capacity();
            buffer.putInt(fileNameLen);
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
            Logger.info("Client文件名称长度发送完成：" + fileNameLen);

            //发送文件的名称
            socketChannel.write(fileNamebuffer);
            fileNamebuffer.clear();
            Logger.info("Client文件名称发送完毕：" + destFile);

            //发送文件长度
            buffer.putLong(file.length());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
            Logger.info("Client文件的长度发送完毕",file.length());

            //发送文件具体的内容
            Logger.debug("Client开始传输文件~~");
            int length = 0;
            long progress = 0;//表示进度
            while((length = inputChannel.read(buffer)) != -1) {
                buffer.flip();
                progress += length;
                while((length = socketChannel.write(buffer)) != 0) {}
                buffer.clear();
                //记录每一次的进度
                Logger.debug("| "+(100*progress/file.length())+"% |");
            }

            //开始关闭各种文件
            IOUtil.closeQuietly(inputChannel);
            //todo 为什么idea隔一会儿就要分别再对settings中的compiler进行设置
            //todo 还有project structure中的相关进行设置，怎么样才能只设置一次？？？
            socketChannel.shutdownOutput();//向外写
            IOUtil.closeQuietly(socketChannel);
            Logger.debug("============文件传输成功===========");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    /**
     * 向服务端传输文件
     *
     * @throws Exception
     */
    public void sendFile()
    {
        try
        {
            String sourcePath = NioDemoConfig.SOCKET_SEND_FILE;
            String srcPath = IOUtil.getResourcePath(sourcePath);
            Logger.debug("srcPath=" + srcPath);

            String destFile = NioDemoConfig.SOCKET_RECEIVE_FILE;
            Logger.debug("destFile=" + destFile);

            File file = new File(srcPath);
            if (!file.exists())
            {
                Logger.debug("文件不存在");
                return;
            }
            FileChannel fileChannel = new FileInputStream(file).getChannel();

            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.socket().connect(
                    new InetSocketAddress(NioDemoConfig.SOCKET_SERVER_IP
                            , NioDemoConfig.SOCKET_SERVER_PORT));
            socketChannel.configureBlocking(false);
            Logger.debug("Client 成功连接服务端");

            while (!socketChannel.finishConnect())
            {
                //不断的自旋、等待，或者做一些其他的事情
            }

            //发送文件名称
            ByteBuffer fileNameByteBuffer = charset.encode(destFile);

            ByteBuffer buffer = ByteBuffer.allocate(NioDemoConfig.SEND_BUFFER_SIZE);
            //发送文件名称长度
            int fileNameLen =     fileNameByteBuffer.limit();
            buffer.putInt(fileNameLen);
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
            Logger.info("Client 文件名称长度发送完成:",fileNameLen);

            //发送文件名称

            socketChannel.write(fileNameByteBuffer);
            Logger.info("Client 文件名称发送完成:",destFile);
            //发送文件长度
            buffer.putLong(file.length());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
            Logger.info("Client 文件长度发送完成:",file.length());


            //发送文件内容
            Logger.debug("开始传输文件");
            int length = 0;
            long progress = 0;
            while ((length = fileChannel.read(buffer)) > 0)
            {
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
                progress += length;
                Logger.debug("| " + (100 * progress / file.length()) + "% |");
            }

            if (length == -1)
            {
                IOUtil.closeQuietly(fileChannel);
                socketChannel.shutdownOutput();
                IOUtil.closeQuietly(socketChannel);
            }
            Logger.debug("======== 文件传输成功 ========");
        } catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        new NioSendClient().sendFile();
    }
}
