package com.work.qq_system_springboot.demo;

import com.alibaba.fastjson.JSONObject;
import com.work.qq_system_springboot.tools.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class JSONTest {


    /**@param ip 192.168.1.24
     *
     * @return byte[] : [-64,-88,1,24]
     */
    public static byte[] ipToByteArray(String ip) {
        if (StringUtils.isBlank(ip)) {
            return null;
        }
        String[] split = ip.split("\\.");
        byte[] bs = new byte[4];
        for (int i=0; i < split.length; i++) {
            bs[i] = (byte)Integer.parseInt(split[i]);
        }
        return bs;
    }

    /**@param bs : [-64,-88,1,24]
     *
     *@return IP 192.168.1.24
     */
    public static String byteArrayToIp(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        for (int i=0 ; i < bs.length; i++) {
            if(i != bs.length - 1){
                sb.append(bs[i] & 0xff).append(".");
            }else{
                sb.append(bs[i] & 0xff);
            }
        }
        return sb.toString();
    }


    @Test
    public void testServer() throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        Socket socket = serverSocket.accept();
        InputStream inputStream = socket.getInputStream();
        DataInputStream dis = new DataInputStream(inputStream);

        byte[] bytes = FileUtils.streamToByteArray(dis);
        FileOutputStream fos = new FileOutputStream("D:\\work\\temp\\aaa.txt");
        fos.write(bytes);
        fos.close();

        dis.close();
        inputStream.close();
        socket.close();
        serverSocket.close();
    }

    @Test
    public void testClient() throws Exception {
        Socket socket = new Socket("192.168.0.106",8080);

        FileInputStream fis = new FileInputStream("D:\\work\\temp\\FileUtils.java");
        byte[] bytes = FileUtils.streamToByteArray(fis);
        fis.close();

        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(outputStream);
        dos.write(bytes);

        dos.close();
        socket.close();
    }

    @Test
    public void testUdpcReceiver() throws IOException {
        DatagramSocket socket = new DatagramSocket(9999);

        //把数据封装成packet
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf,buf.length);
        //接收数据
        socket.receive(packet);
        int length = packet.getLength();
        byte[] data = packet.getData();
        String res = new String(data,0,length);
        System.out.println(res);
        //发送数据
        data = "我已经收到回复".getBytes();
        packet = new DatagramPacket(data,data.length,InetAddress.getByName("localhost"),9998);
        socket.send(packet);

        socket.close();
    }

    @Test
    public void testUdpSender() throws IOException {
        DatagramSocket socket = new DatagramSocket(9998);

        byte[] bytes = "明天吃火锅，几点开始呢？".getBytes();
        //将数据封装成packet
        DatagramPacket packet = new DatagramPacket(
                bytes,bytes.length,InetAddress.getByName("localhost"),9999);

        socket.send(packet);

        socket.receive(packet);

        String res = new String(bytes, 0, bytes.length);
        System.out.println(res);

        socket.close();
    }
}
