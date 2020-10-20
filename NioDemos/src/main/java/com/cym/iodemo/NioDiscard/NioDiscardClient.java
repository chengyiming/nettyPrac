package com.cym.iodemo.NioDiscard;

import com.cym.NioDemoConfig;
import com.cym.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NioDiscardClient {
    private Selector selector;
    private SocketChannel socketChannel;

    NioDiscardClient() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.info("初始化客户端失败......");
        }
    }

    public void startClient() {
        try {
            //socketChannel.configureBlocking(false);
            socketChannel.socket().connect(new InetSocketAddress(NioDemoConfig.SOCKET_SERVER_IP,
                    NioDemoConfig.SOCKET_SERVER_PORT));
            while(!socketChannel.finishConnect()){}
            Logger.info("连接到了服务端....");
            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNext()) {
                String str = scanner.next();
                //需要发送的内容
                ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
                int len = 0;
                while((socketChannel.write(buffer)) != 0) {}
                Logger.info("本条信息"+str+"发送完毕.....");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NioDiscardClient().startClient();
    }
}
