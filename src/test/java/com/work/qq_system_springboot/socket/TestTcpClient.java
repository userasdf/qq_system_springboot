package com.work.qq_system_springboot.socket;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

public class TestTcpClient {

    @Test
    public void test() throws IOException {
        //指定服务端的主机地址以及端口号
        Socket socket = new Socket("192.168.1.103",8080);

        //获取输出流
        OutputStream os = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        //发送数据
        dos.writeUTF("我是客户端");

        //关闭资源
        dos.close();
        socket.close();
    }

}
