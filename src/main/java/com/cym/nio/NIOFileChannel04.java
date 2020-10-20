package com.cym.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @Date 2020-10-04
 * @author cym
 * @description 使用transferFrom方法拷贝文件
 * transferFrom(ReadableByteChannel src, long position, long  count) 从目标通道复制数据到当前通道
 * transferTo(long position, long count, WritableByteChannel targer) 把数据从当前通道复制到目标通道
 */
public class NIOFileChannel04 {
    public static void main(String[] args) throws Exception{
        File file = new File("from.jpg");
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel inputStreamChannel = fileInputStream.getChannel();
        FileOutputStream fileOutputStream = new FileOutputStream("to.jpg");
        FileChannel outputStreamChannel = fileOutputStream.getChannel();
        outputStreamChannel.transferFrom(inputStreamChannel, 0, file.length());
        fileInputStream.close();
        fileOutputStream.close();
    }
}
