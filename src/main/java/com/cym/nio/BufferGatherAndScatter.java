package com.cym.nio;

/**
 * @Date 2020-10-04
 * @author cym
 * @description
 * buffer的分散和聚合，在数据比较多的时候，我们可以使用多个buffer
 * scatter : 将数据写入到buffer的时候，可以使用Buffer数组一次写入，也就是当一个buffer满了之后
 * 可以换成下一个
 * gathering :从buffer中读数据的时候，可以使用buffer数组， 依次读
 *
 * 本例中我们演示的是serverSocketChannel 和socketChannel
 */
public class BufferGatherAndScatter {
    public static void main(String[] args) {

    }
}
