package com.moscase.socket;

import android.util.Log;  
import java.io.IOException;  
import java.net.InetSocketAddress;  
import java.nio.channels.SelectionKey;  
import java.nio.channels.Selector;  
import java.nio.channels.SocketChannel;  
  
public class TCPClient {  
    // 信道选择器  
    private Selector selector;  
  
    // 与服务器通信的信道  
    SocketChannel socketChannel;  
  
    // 要连接的服务器Ip地址  
    private String hostIp;  
  
    // 要连接的远程服务器在监听的端口  
    private int hostListenningPort;  
    String imei;  
  
    public TCPClient(String HostIp, int HostListenningPort, String imei) throws IOException {  
        this.hostIp = HostIp;  
        this.hostListenningPort = HostListenningPort;  
        this.imei = imei;  
        initialize();  
    }  
  
  
    /** 
     * 初始化 
     * 
     * @throws IOException 
     */  
    private void initialize() throws IOException {  
        try {  
            // 获得一个Socket通道  
            SocketChannel channel = SocketChannel.open();  
            // 设置通道为非阻塞  
            channel.configureBlocking(false);  
            // 获得一个通道管理器  
            this.selector = Selector.open();  
  
            // 客户端连接服务器,其实方法执行并没有实现连接，需要在listen（）方法中调  
            //用channel.finishConnect();才能完成连接  
            channel.connect(new InetSocketAddress(hostIp, hostListenningPort));  
            //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件。  
            channel.register(selector, SelectionKey.OP_CONNECT);  
  
        } catch (Exception e) {  
            Log.d("a", e.toString());  
        }  
        // 启动读取线程  
        new TCPClientReadThread(selector, imei);  
    }  
}  