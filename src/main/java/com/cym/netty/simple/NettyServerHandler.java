package com.cym.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;


/*
1.自定义一个handler，需要继承netty规定好的某个handlerAdapter
这时这个handler才能成为是handler
2.因为有很多的规范需要遵守

 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    //读取数据事件（读取客户端发送的消息）
    /*
    1.channelHandlerContext : ctx上下文对象，含有管道pipeline，通道socketChannel
    管道有更多的handler进行处理业务处理，通道关注得时数据的读写，连接地址
    2.objext msg :客户端发送的数据，默认是object类型
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server ctx = " + ctx);
        /*
        将msg转成一个Bytebuf
        这个类是netty提供的，不是NIO中的ByteBuffer,功能要更加强大
         */
//        ByteBuf buf = (ByteBuf)msg;
//        System.out.println("客户端发送的消息是："+buf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端的地址是"+ctx.channel().remoteAddress());
        System.out.println("客户端："+ctx.channel().remoteAddress()+
                "线程："+Thread.currentThread().getName());
        ctx.channel().eventLoop().execute(()->{
            try {
                System.out.println("任务一线程名称："+Thread.currentThread().getName());
                Thread.sleep(5*1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端2", CharsetUtil.UTF_8));
            }catch(Exception ex) {
                System.out.println("发生了异常" + ex.getMessage());
            }
        });
//
//        ctx.channel().eventLoop().execute(()->{
//            try {
//                System.out.println("任务二线程名称："+Thread.currentThread().getName());
//                Thread.sleep(5*1000);
//                ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端3", CharsetUtil.UTF_8));
//            }catch(Exception ex) {
//                System.out.println("发生了异常" + ex.getMessage());
//            }
//        });
        Thread.sleep(5*1000);
        System.out.println("继续执行....");
    }

    /*
   数据读取完毕之后
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        /*
        将数据写入到缓存中并刷新缓存，将数据刷到通道里面
        相当于write + flush
        发送的数据需要进行编码
         */
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端1", CharsetUtil.UTF_8));
    }

    /*
    处理可能发生的异常
    发生了异常，关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
