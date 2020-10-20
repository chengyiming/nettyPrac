package com.cym.nio.channel.filechannel;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用FileChannel实现复制功能
 * 将源文件复制到目标文件中
 */
public class FileChannelDemo {
    public static void main(String[] args) {
        try {
            RandomAccessFile rw = new RandomAccessFile("from.jpg", "rw");
            RandomAccessFile copyRw = new RandomAccessFile("toto.jpg", "rw");
            FileChannel channel = rw.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(5);
            int length = 0;
            //读channel写buffer
            FileChannel copyRwChannel = null;
            while((length = channel.read(buffer)) != -1) {
                //开始读模式
                buffer.flip();
                copyRwChannel = copyRw.getChannel();
                while((length = copyRwChannel.write(buffer)) != 0) {}
                buffer.clear();
            }
            copyRwChannel.force(true);//强制刷新到磁盘中
            channel.close();
            copyRwChannel.close();
            rw.close();
            copyRw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
