package com.cym.iodemo.reactor.echo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;


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
    private static final Charset charset = Charset.forName("UTF-8");

    EchoHandler(Selector selector, SocketChannel socketChannel) throws Exception{
        this.socketChannel = socketChannel;
        socketChannel.configureBlocking(false);
        //仅仅取得选择键，之后设置感兴趣的事件
        sk = socketChannel.register(selector, SelectionKey.OP_READ);
        sk.attach(this);
        /*sk.interestOps(SelectionKey.OP_READ);
        selector.wakeup();*/
    }

    @Override
    public void run() {
        try {
            /*if(state == SENDING) {
                System.out.println("写");
                //写入通道
                socketChannel.write(buffer);
                //写完之后，从通道读,buffer切换成写入模式
                buffer.clear();
                //写完之后，注册read就绪事件
                sk.interestOps(SelectionKey.OP_READ);
                state = RECEIVING;
                System.out.println("写完");
            } else if(state == RECEIVING) {
                System.out.println("读");
                //从通道进行读取
                int length = 0;
                //TODO 读通道，写buffer
                //这里我最开始使用得时 ！= -1，然后换成了0
                buffer.clear();
                while((length = socketChannel.read(buffer)) > 0) {
                    System.out.println("server get :"+
                            new String(buffer.array(), charset));
                    System.out.println("length:"+length);
                }
                //bytebuffer切换成读取模式,准备写入通道
                buffer.flip();
                //读完之后，注册write就绪事件
                sk.interestOps(SelectionKey.OP_WRITE);
                state = SENDING;
                System.out.println("读完");
            }*/
            if(state == RECEIVING) {
                System.out.println("读");
                //从通道进行读取
                int length = 0;
                //TODO 读通道，写buffer
                //这里我最开始使用得时 ！= -1，然后换成了0
                buffer.clear();
                while((length = socketChannel.read(buffer)) > 0) {
                    System.out.println("server get :"+
                            new String(buffer.array(), charset));
                    System.out.println("length:"+length);
                }
                //bytebuffer切换成读取模式,准备写入通道
                buffer.flip();
                //读完之后，注册write就绪事件
                /*sk.interestOps(SelectionKey.OP_WRITE);
                state = SENDING;
                System.out.println("读完");*/
            }
            //TODO 处理结束，不能关闭，需要重复使用, 客户端需要重复发送
            //sk.cancel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
