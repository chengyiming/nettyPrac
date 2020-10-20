package com.cym;

import com.cym.util.ConfigProperties;

import javax.rmi.PortableRemoteObject;

public class NioDemoConfig extends ConfigProperties {
    static ConfigProperties prop = new NioDemoConfig("/system.properties");
    public NioDemoConfig(String classPath) {
        super(classPath);
        super.loadFromFile();
    }

    public static final String FILE_SRC_PATH
            = prop.getValue("");
    public static final String SOCKET_RECEIVE_PATH
            = prop.getValue("socket.receive.path");



    //发送文件相关
    public static final String SOCKET_RECEIVE_FILE
            = prop.getValue("socket.receive.file");
    public static final String SOCKET_SEND_FILE
            = prop.getValue("socket.send.file");

    //连接相关
    public static final String SOCKET_SERVER_IP
            = prop.getValue("socket.server.ip");

    public static final int SOCKET_SERVER_PORT
            = prop.getIntValue("socket.server.port");



    //buffer大小相关
    public static final int SEND_BUFFER_SIZE
            = prop.getIntValue("send.buffer.size");
    public static final int SERVER_BUFFER_SIZE
            = prop.getIntValue("server.buffer.size");

    public static final int CORE_POOL_SIZE
            = prop.getIntValue("core.pool.size");
    public static final int MAXMUM_POOL_SIZE
            = prop.getIntValue("maxmum.pool.size");
    public static final int KEEPALIVE_TIME
            = prop.getIntValue("keepalive.time");
}
