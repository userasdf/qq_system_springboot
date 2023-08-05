package com.work.qq_system_springboot.controller;

import com.work.qq_system_springboot.component.WebSocketServer;
import com.work.qq_system_springboot.entity.Message;
import com.work.qq_system_springboot.tools.QQSystemUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

@Controller
public class TestController {



    @RequestMapping("/getRemoteHost")
    @ResponseBody
    public List<Object> getRemoteHost(HttpServletRequest request){
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(QQSystemUtil.getClientRealIp(request));
        objects.add(QQSystemUtil.getOuterNetIp());
        objects.add(QQSystemUtil.getClientHostName(request));
        objects.add(QQSystemUtil.getClientBrowserName(request));
        objects.add(QQSystemUtil.getUserAgent(request));
        return objects;
    }

    @RequestMapping("/sendFile")
    public String sendFile(){return "demo/sendFile";}

    @RequestMapping("/get")
    @ResponseBody
    public Set<String> get(){
        Map<String, Session> map = WebSocketServer.getMap();
        return map.keySet();
    }

    @RequestMapping("/push")
    @ResponseBody
    public void push() throws IOException {
        Session session = WebSocketServer.getSession("118");
        session.getBasicRemote().sendText("sefsef");
        WebSocketServer.sendMessage(new Message(),session);
    }


    @RequestMapping("/test")
    public void test(String fileName, HttpServletResponse response) throws Exception {

        String fileRoot = "d:/work";

        //中文乱码处理
        fileName = URLDecoder.decode(fileName,"utf8");

        //模拟文件路径
        String str = fileRoot+"/"+fileName;

        System.out.println(str);

        //获取输入流
        InputStream bis = new BufferedInputStream(new FileInputStream(new File(str)));
        //转码，免得文件名中文乱码
        fileName = URLEncoder.encode(fileName,"UTF-8");
        //设置文件下载头
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        //设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data");
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while((len = bis.read()) != -1){
            out.write(len);
        }
        out.flush();
        out.close();

    }

}
