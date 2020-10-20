package com.cym.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Date 2020-10-04
 * @author cym
 * @description
 * 从文件中读取数据
 */
public class NIOFileChannel02 {
    public static void main(String[] args) throws Exception{
      File file = new File("d:\\java\\code\\CYM_code\\nettyFile");
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel = fileInputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());
        fileChannel.read(byteBuffer);
        //反转
        byteBuffer.flip();
        //读取缓冲区的内容,这里显示的是单个字节
//        byte[] dest = new byte[512];
//        byteBuffer.get(dest);
//        for(byte b : dest) {
//            System.out.print(b);
//        }
        //将字节转变成String，byteBuffer.array()可以统一取出缓冲区所有的字节
        System.out.println(new String(byteBuffer.array()));
        fileInputStream.close();
    }
}
