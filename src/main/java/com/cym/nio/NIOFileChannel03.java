package com.cym.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Date 2020-10-04
 * @author cym
 * @description 使用buffer完成文件的读取，从一个文件中拷贝，然后再放到另一个文件中
 */
public class NIOFileChannel03 {
    public static void main(String[] args) throws Exception{
        //读取文件
        File file = new File("from.jpg");
        FileInputStream fileInputStream = new FileInputStream(file);
        //这个通道同时进行输入和输出
        FileChannel fileChannel = fileInputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        //写入文件
        FileOutputStream fileOutputStream = new FileOutputStream("to3.jpg");
        FileChannel outChannel = fileOutputStream.getChannel();

        while(true) {
            byteBuffer.clear();
            int read = fileChannel.read(byteBuffer);
            System.out.println("read="+read);
            if(read == -1) {
                break;
            }
            byteBuffer.flip();
            int wlen = 0;
            while((wlen = outChannel.write(byteBuffer)) > 0) {
                System.out.println("wlen = " + wlen);
            }
            System.out.println(wlen);
        }
        fileInputStream.close();
        fileOutputStream.close();
        //------------------我觉得还可以使用一些比较简单的通道方法----

    }
}
