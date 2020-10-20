package com.cym.nio;

import java.nio.ByteBuffer;

/**
 * @Date 2020-10-04
 * @author cym
 * @description
 * 将一个buffer转换成只读Buffer
 */
public class ReadOnlyBuffer {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(65);
        for(int i = 0; i < 64; i++) {
            System.out.println((byte)i);
            buffer.put((byte)i);
        }
        //在进行类型转换的时候，直接从低位开始截取8位
        buffer.put((byte)1025);
        //进行读取
        buffer.flip();

        //得到一个只读的buffer
        ByteBuffer buffer1 = buffer.asReadOnlyBuffer();
        //只读的buffer没有这个方法，看代码
        //System.out.println(new String(buffer1.array()));
        while(buffer1.hasRemaining()) {
            System.out.println(buffer1.get());
        }
    }
}
