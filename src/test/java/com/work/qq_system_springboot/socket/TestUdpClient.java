package com.work.qq_system_springboot.socket;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;

public class TestUdpClient {

    @Test
    public void test() throws IOException {
        DatagramSocket socket = new DatagramSocket(8888);

        //封装数据
        byte[] bytes = "你好，我是发送端".getBytes();
        DatagramPacket packet = new DatagramPacket(bytes,bytes.length, InetAddress.getByName("192.168.1.103"),8888);

        //发送数据
        socket.send(packet);

        socket.close();
    }

}
