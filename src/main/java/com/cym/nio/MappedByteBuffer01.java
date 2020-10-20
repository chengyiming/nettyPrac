package com.cym.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * MappedByteBuffer：
 * 可以让文件直接在内存（堆外的内存）中进行修改，如何同步到文件由NIO来完成
 * 感觉就像是内存映射，也就是磁盘和某一块内存进行了映射
 */
public class MappedByteBuffer01 {
    public static void main(String[] args) throws Exception{
        RandomAccessFile rw = new RandomAccessFile("d:\\java\\code\\CYM_code\\nettyFile", "rw");
        FileChannel channel = rw.getChannel();
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE , 0, 5);
        map.put(0, (byte)'H');
        channel.close();
        rw.close();
    }
}
