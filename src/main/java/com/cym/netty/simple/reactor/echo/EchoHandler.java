package com.cym.netty.simple.reactor.echo;

import com.sun.org.slf4j.internal.LoggerFactory;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;


/**
 * @author cym
 * @Date 2020-10-10
 * 这个是进行业务处理的类
 */
public class EchoHandler implements Runnable {
    final SocketChannel socketChannel;
    final SelectionKey sk;
    final ByteBuffer buffer = ByteBuffer.allocate(1024);
    final int RECEIVING = 0, SENDING = 1;
    int state = RECEIVING;

    EchoHandler(Selector selector, SocketChannel socketChannel) throws Exception{
        this.socketChannel = socketChannel;
        socketChannel.configureBlocking(false);
        //仅仅取得选择键，之后设置感兴趣的事件
        sk = socketChannel.register(selector, 0);
        sk.attach(this);
        //TODO 这个是不是表示读就绪了
        sk.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }
    public void run() {
        try {
            if(state == SENDING) {
                buffer.put("我爱你".getBytes());
                //写入通道
                socketChannel.write(buffer);
                //写完之后，从通道读,buffer切换成写入模式
                buffer.clear();
                //写完之后，注册read就绪事件
                sk.interestOps(SelectionKey.OP_READ);
                state = RECEIVING;
            } else if(state == RECEIVING) {
                //从通道进行读取
                int length = 0;
                //TODO 读通道，写buffer
                //这里我最开始使用得时 ！= -1，然后换成了0
                while((length = socketChannel.read(buffer)) > 0) {
                    System.out.println("写入buffer字符串是："+
                            new String(buffer.array(), 0, length));
                }
                //读完之后，准备写入通道，bytebuffer切换成读取模式
                buffer.flip();
                //读完之后，注册write就绪事件
                sk.interestOps(SelectionKey.OP_WRITE);
                state = SENDING;
            }
            //TODO 处理结束，不能关闭，需要重复使用
            //sk.cancel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
