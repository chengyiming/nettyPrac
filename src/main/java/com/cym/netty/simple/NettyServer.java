package com.cym.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.Socket;


/**
 * @author cym
 *
 */
public class NettyServer {
    public static void main(String[] args) throws Exception{
        //创建BossGroup和WorkerGroup
        //创建两个线程组
        //处理连接请求
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //处理真正的业务
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);

        try {
            //创建服务器启动对象，可以配置启动参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //使用链式编程进行设置
            serverBootstrap.group(bossGroup, workerGroup)//设置两个线程组
                    .channel(NioServerSocketChannel.class)//设置服务器通道的类型
                    .option(ChannelOption.SO_BACKLOG, 128)//设置线程队列等待连接的个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            //workerGroup的EventLoop对应的管道设置处理器
            System.out.println("......服务器 is ready");
            //配置完成之后开始绑定端口并且同步处理
            //生成了一个ChannelFutrue对象，这里会立刻返回，
            //启动了服务器
            ChannelFuture channelFuture = serverBootstrap.bind(6668).sync();

            //对关闭通道进行监听，并没有关闭，只是监听当事件发生的时候才执行
            //这里涉及了netty的异步模型
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
