package com.cym.iodemo.socketchannel.problemCase;

import com.cym.util.IOUtil;
import com.cym.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * 这一部分是抄的书籍
 * 文件传输client端
 */
public class NioSendClient2 {
    private Charset charset = Charset.forName("utf-8");

    /**
     * 向服务端传输文件
     *
     * @throws Exception
     */
    public void sendFile()
    {
        try
        {
            String sourcePath = "/system.properties";
            String srcPath = IOUtil.getResourcePath(sourcePath);
            Logger.debug("srcPath=" + srcPath);

            String destFile = "system.dest.properties";
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
                    new InetSocketAddress("localhost"
                            , 16668));
            socketChannel.configureBlocking(false);
            Logger.debug("Client 成功连接服务端");

            while (!socketChannel.finishConnect())
            {
                //不断的自旋、等待，或者做一些其他的事情
            }
            //发送文件名称
            ByteBuffer fileNameByteBuffer = charset.encode(destFile);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //发送文件名称长度
            int fileNameLen = fileNameByteBuffer.limit();
            buffer.putInt(fileNameLen);
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
            Logger.info("Client 文件名称长度发送完成:",fileNameLen);

            //发送文件名称
            socketChannel.write(fileNameByteBuffer);
            Logger.info("Client 文件名称发送完成:",destFile);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NioSendClient2().sendFile();
    }
}
