package com.cym.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Date 2020/10/13
 * @author cym
 * @description
 * 数据的流向：str->bytebuffer->channel[channel包裹了fileoutputStream]->文件
 */
public class NIOFileChannel01 {
    public static void main(String[] args) throws Exception{
        String str = "hello chengyiming";
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\java\\code\\CYM_code\\nettyFile");
        FileChannel fileChannel = fileOutputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();
        fileChannel.write(byteBuffer);
        fileOutputStream.close();
    }
}
