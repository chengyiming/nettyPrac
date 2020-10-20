package com.cym.iodemo.reactor.multi_thread_reactor;

import com.cym.NioDemoConfig;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 读写处理器
 */
public class ReadOrWriteHandler implements Runnable{
    private Charset charset = Charset.forName("UTF-8");
    private SelectionKey selectionKey;
    private final int RECEIVE = 0, SENDING = 1;
    private int state = RECEIVE;
    private SocketChannel socketChannel;
    private ByteBuffer buffer = ByteBuffer.allocate(NioDemoConfig.SERVER_BUFFER_SIZE);

    ReadOrWriteHandler(Selector selector, SocketChannel socketChannel) {
        try {
            this.socketChannel = socketChannel;
            socketChannel.configureBlocking(false);
            this.selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
            selectionKey.attach(this);
            /*selectionKey.interestOps(SelectionKey.OP_READ);
            selector.wakeup();*/

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayBlockingQueue<Runnable> arrayBlockingQueue =
            new ArrayBlockingQueue<>(5);

    private static ThreadPoolExecutor workHandlerThreadPool =
            new ThreadPoolExecutor(NioDemoConfig.CORE_POOL_SIZE,
                    NioDemoConfig.MAXMUM_POOL_SIZE,
                    NioDemoConfig.KEEPALIVE_TIME,
                    TimeUnit.SECONDS,
                    arrayBlockingQueue,
                    new NameableThreadFactory("WORK-HANDLER-POOL")
            );

    @Override
    public void run() {
        workHandlerThreadPool.execute(new Task());
    }


    /*class Task implements Runnable{

        @Override
        public void run() {
            echo();
        }

        private void echo(){
            System.out.println(Thread.currentThread().getName()+"comes...");
            try {
                synchronized (ReadOrWriteHandler.class) {
                    if(state == RECEIVE) {
                        System.out.println(Thread.currentThread().getName()+"读");
                        int length = 0;
                        while((length = socketChannel.read(buffer)) > 0) {
                            System.out.println(Thread.currentThread().getName()+" get from "+socketChannel.getRemoteAddress()+
                                    ".... content:"+
                                    new String(buffer.array(), charset));
                        }
                        buffer.flip();
                        selectionKey.interestOps(SelectionKey.OP_WRITE);
                        state = SENDING;
                        System.out.println(Thread.currentThread().getName()+"读完");
                    } else if(state == SENDING){
                        System.out.println(Thread.currentThread().getName()+"写");
                        //从buffer中读
                        int length = 0;
                        while((length = socketChannel.write(buffer)) != 0) {
                            System.out.println(Thread.currentThread().getName()+" send to "+
                                    socketChannel.getRemoteAddress()+ " content:" + new String(buffer.array(), charset));
                        }
                        buffer.clear();
                        selectionKey.interestOps(SelectionKey.OP_READ);
                        state = RECEIVE;
                        System.out.println(Thread.currentThread().getName()+"写完");
                    }
                }
            } catch (IOException e) {
                selectionKey.cancel();
                System.out.println("客户端断开了连接....");
            }

        }
    }*/

    private synchronized void echo(){
        System.out.println(Thread.currentThread().getName()+"comes...");
        try {

            if(state == RECEIVE) {
                System.out.println(Thread.currentThread().getName()+"读");
                int length = 0;
                while((length = socketChannel.read(buffer)) > 0) {
                    System.out.println(Thread.currentThread().getName()+" get from "+socketChannel.getRemoteAddress()+
                            ".... content:"+
                            new String(buffer.array(), charset));
                }
                buffer.flip();
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                state = SENDING;
                System.out.println(Thread.currentThread().getName()+"读完");
            } else if(state == SENDING){
                System.out.println(Thread.currentThread().getName()+"写");
                //从buffer中读
                int length = 0;
                while((length = socketChannel.write(buffer)) != 0) {
                    System.out.println(Thread.currentThread().getName()+" send to "+
                            socketChannel.getRemoteAddress()+ " content:" + new String(buffer.array(), charset));
                }
                buffer.clear();
                selectionKey.interestOps(SelectionKey.OP_READ);
                state = RECEIVE;
                System.out.println(Thread.currentThread().getName()+"写完");
            }

        } catch (IOException e) {
            selectionKey.cancel();
            System.out.println("客户端断开了连接....");
        }

    }
    class Task implements Runnable {

        @Override
        public void run() {
            ReadOrWriteHandler.this.echo();
        }
    }

}






