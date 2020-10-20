package com.cym.nio.channel.filechannel;

import sun.misc.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用输入和输出流编写
 * 同时使用标准的文件路径
 * 文件并不需要事先就存在
 */
public class FileChannelDemo2 {
    public static void main(String[] args) {
        /*File srcFile = new File("from.jpg");
        File destFile = new File("to2.jpg");*/
        try {
            FileInputStream fileInputStream = new FileInputStream("from.jpg");
            FileChannel inputStreamChannel = fileInputStream.getChannel();
           /* if(!destFile.exists()) {
                destFile.createNewFile();//如果不存在就创建
            }*/
            FileOutputStream fileOutputStream = new FileOutputStream("to2.jpg");
            FileChannel outChannel = fileOutputStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(5);
            int length = 0;
            while((length = inputStreamChannel.read(buffer))!= -1) {
                buffer.flip();
                while((length = outChannel.write(buffer))!= 0){};
                buffer.clear();
            }
            //读取完毕，进行关闭
            outChannel.force(true);

            inputStreamChannel.close();
            outChannel.close();
            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
