package com.cym.nio.buffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class RewindTest {
    public static void main(String[] args) {
        IntBuffer buffer = IntBuffer.allocate(1024);
        for(int i = 0; i < 5; i++) {
            buffer.put(i);
        }
        buffer.flip();
        for(int i = 0; i < 2; i++) {
            int content = buffer.get();
            System.out.println(content);
        }
        print(buffer);
        buffer.rewind();
        print(buffer);
        //从读模式变成写模式
        buffer.clear();
        print(buffer);
     }

     private static void print(Buffer buffer) {
         System.out.println("position=" + buffer.position());
         System.out.println("limit=" + buffer.limit());
         System.out.println("capacity="+ buffer.capacity());
         System.out.println("mark="+buffer.mark());
     }
}
