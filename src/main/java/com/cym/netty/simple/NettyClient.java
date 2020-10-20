package com.cym.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/*
练习的客户端代码
 */
public class NettyClient {
    /*
    客户端需要一个事件循环组

     */
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        //创建客户端的启动对象
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(eventExecutors)//设置线程组
                    .channel(NioSocketChannel.class)//客户端通道的实现类，反射创建
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyClientHandler());   //加入客户端的处理器
                        }
                    });
            System.out.println("客户端 is ok");
            //启动客户端应该进行连接
            //.sync()立即得到结果，channelFutrue涉及到netty的异步模型
            ChannelFuture channelFuture = bootstrap.connect("localhost", 6668).sync();
            //对变比的通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventExecutors.shutdownGracefully();
        }


    }
}
