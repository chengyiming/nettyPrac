package com.cym.nio.channel.filechannel;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 使用transferFrom的方式进行拷贝
 * 不要outputStream的文件存在
 */
public class FastFileChannel1 {
    public static void main(String[] args) {
        try {
            File file = new File("from.jpg");
            FileInputStream fileInputStream = new FileInputStream(file);
            FileChannel inputStreamChannel = fileInputStream.getChannel();
            FileOutputStream fileOutputStream = new FileOutputStream("to_fast.jpg");
            FileChannel outputStreamChannel = fileOutputStream.getChannel();
            outputStreamChannel.transferFrom(inputStreamChannel, 0, file.length());
            outputStreamChannel.force(true);//进行刷新
            inputStreamChannel.close();
            outputStreamChannel.close();
            fileInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
