package com.work.qq_system_springboot.demo;

import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class TestSender {

    @Test
    public void test() throws IOException {

        Socket socket = new Socket("192.168.1.103",8080);
        InputStream is = socket.getInputStream();
        DataInputStream dis = new DataInputStream(is);

        String res = dis.readUTF();
        System.out.println(res);

        dis.close();
        socket.close();


    }

}
