package com.work.qq_system_springboot.component;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.qq_system_springboot.entity.Message;
import com.work.qq_system_springboot.entity.OprLog;
import com.work.qq_system_springboot.service.*;
import com.work.qq_system_springboot.tools.QQSystemConstant;
import com.work.qq_system_springboot.tools.QQSystemUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.work.qq_system_springboot.tools.QQSystemConstant.LOG_TYPE_FILE;
import static com.work.qq_system_springboot.tools.QQSystemConstant.LOG_TYPE_INTERACTIVE;

@ServerEndpoint(value = "/imserver/{sessionId}")
@Component
public class WebSocketServer {


    private static ConfigurableApplicationContext applicationContext;

    public static void setApplicationContext(ConfigurableApplicationContext applicationContext){
        WebSocketServer.applicationContext = applicationContext;
    }

    //获取操作日志服务
    public OprLogService getOprLogService(){
        return applicationContext.getBean(OprLogService.class);
    }

    //获取用户服务
    public UserService getUserService(){
        return applicationContext.getBean(UserService.class);
    }

    //获取群组服务
    public GroupService getGroupService(){
        return applicationContext.getBean(GroupService.class);
    }

    private static String sendFilePath;
    @Value("${community.send_file.path}")
    public void setSendFilePath(String sendFilePath){
        WebSocketServer.sendFilePath = sendFilePath;
    }

    public static Map<String,Session> getMap(){
        return map;
    }

    private static Map<String,Session> map = new ConcurrentHashMap<>();

    private String sessionId;

    //对外开放获取map元素的方法
    public static Session getSession(String sessionId){
        return map.get(sessionId);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId")String sessionId){

        map.put(sessionId,session);
        this.sessionId = sessionId;
        System.out.println("新会话："+sessionId+",共"+map.keySet().size()+"会话");

    }

    @OnMessage
    public void onMessage(String msg,Session session) throws IOException {

        System.out.println("接收到客户端发送的msg："+msg);

        //读取客户端发来的消息
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(msg, Message.class);
        message.setCreateTime(new Date());//设置发送时间
        //设置ip地址
        InetSocketAddress remoteAddress = WebSocketUtil.getRemoteAddress(session);
        String ipAddress = remoteAddress.getHostString();
        message.setIpAddress(ipAddress);

        //判断消息类型，并做相应的处理
        if(message.getTopic().equals(QQSystemConstant.TOPIC_MSG)||
                message.getTopic().equals(QQSystemConstant.TOPIC_FILE)){
            //私聊或群发消息
            System.out.println("私聊或发送消息");
            forwardMessage(message);
        }else if(message.getTopic().equals(QQSystemConstant.TOPIC_ADD_RELATION)){
            //添加好友或群聊
            System.out.println("添加好友或群聊");
            addRelation(message);
        }
    }

    @OnClose
    public void onClose(){
        System.out.println("会话："+this.sessionId+"关闭了socket连接");
        map.remove(this.sessionId);
    }

    @OnError
    public void onError(Throwable throwable){
        System.out.println("客户端错误:"+throwable.getMessage());
    }

    //发送消息
    public static void sendMessage(Message message,Session session){
        try {
            String jsonMsg = JSON.toJSONString(message);
            session.getBasicRemote().sendText(jsonMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //转发私聊消息或群聊消息
    public void forwardMessage(Message message) throws IOException {


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(message.getIpAddress());
        oprLog.setUserId(message.getFromId());
        oprLog.setUserName(getUserService().findById(message.getFromId()).getUsername());
        oprLog.setType(LOG_TYPE_INTERACTIVE);


        //1、先把消息转发到客户端
        if(message.getIsGroup()==0){//不是群聊(只需要发送对方与自己)
            //记录日志
            String targetName = getUserService().findById(message.getToId()).getUsername();
            oprLog.setInfo("向好友："+targetName+"发送消息："+message.getContent());
            if(message.getTopic().equals(QQSystemConstant.TOPIC_FILE)){
                oprLog.setInfo("向好友："+targetName+"发送了一张图片");
            }
            //消息的转发
            //先发给目标用户
            Session session = getSession(String.valueOf(message.getToId())
                    +"_"+String.valueOf(message.getFromId()));
            if(session!=null){
                message.setStatus(1);//如果用户在线（在当前的聊天页面），那么发送的消息就是已读的
                WebSocketServer.sendMessage(message,session);
            }else{
                //不再当前的聊天页面，可能没登录，或在其他页面
                message.setStatus(0);
                //转发目标用户的header对应的socket
                session = getSession(String.valueOf(message.getToId())+"_header");
                if(session!=null){
                    WebSocketServer.sendMessage(message,session);
                }
                //转发目标用户的friendList对应的socket
                session = getSession(String.valueOf(message.getToId())+"_friendList");
                if(session!=null){
                    WebSocketServer.sendMessage(message,session);
                }
            }
            //再发给当前用户
            session = getSession(String.valueOf(message.getFromId())+"_"+String.valueOf(message.getToId()));
            WebSocketServer.sendMessage(message,session);
        }else{//群发消息（获取当前群的所有用户id）
            String targetName = getGroupService().findById(message.getToId()).getGroupName();
            oprLog.setInfo("在一个群聊："+targetName+"里面说："+message.getContent());
            if(message.getTopic().equals(QQSystemConstant.TOPIC_FILE)){
                oprLog.setInfo("在一个群聊："+targetName+"里面发了一张图片");
            }
            RelationService relationService = applicationContext.getBean(RelationService.class);
            List<Integer> allUserByGroupId = relationService.getAllUserByGroupId(message.getToId(),0);//level=0代表普通用户级别以上(等价于所有用户)
            Iterator<Integer> iterator = allUserByGroupId.iterator();
            while (iterator.hasNext()){
                Session session = getSession(String.valueOf(iterator.next())
                        +"_"+String.valueOf(message.getToId()));
                if(session!=null){
                    WebSocketServer.sendMessage(message,session);
                }
            }
        }
        //2、再从数据库写入数据
        MessageService messageService = applicationContext.getBean(MessageService.class);
        //如果为文件信息
        if(message.getTopic().equals(QQSystemConstant.TOPIC_FILE)){
            //设置随机文件名
            String fileName = QQSystemUtil.generateUUID()+".txt";
            String path = sendFilePath+"/"+fileName;
            FileOutputStream fos = new FileOutputStream(path);
            byte[] bytes = message.getContent().getBytes();
            fos.write(bytes,0,bytes.length);
            message.setContent(fileName);//文本信息为文件的名字
        }
        //向数据库插入一条数据
        messageService.sendMessage(message);
        //记录日志
        getOprLogService().addOprLog(oprLog);
    }

    //添加好友或添加群聊
    public void addRelation(Message message){


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(message.getIpAddress());
        oprLog.setUserId(message.getFromId());
        oprLog.setUserName(getUserService().findById(message.getFromId()).getUsername());
        oprLog.setType(LOG_TYPE_INTERACTIVE);




        RelationService relationService = applicationContext.getBean(RelationService.class);
        if(message.getIsGroup()==0){
            //记录日志
            String targetName = getUserService().findById(message.getToId()).getUsername();
            oprLog.setInfo("请求添加好友："+targetName+"，验证消息："+message.getContent());

            //添加好友
            Session session = getSession(String.valueOf(message.getToId())+"_header");//头部的socket连接
            if(session!=null){
                //提醒该用户有人向他发送了消息
                sendMessage(message,session);
                System.out.println("成功提醒客户端，有新好友");
            }
            relationService.addFriend(message.getFromId(),message.getToId(),message.getContent());
        }else{

            //记录日志
            String targetName = getGroupService().findById(message.getToId()).getGroupName();
            oprLog.setInfo("请求添加群聊："+targetName+"，验证消息："+message.getContent());

            //添加群聊
            System.out.println("添加群聊！！！");
            //通知所有管理员
            List<Integer> allManagers = relationService.getAllUserByGroupId(message.getToId(), 1);//管理员级别以上的用户都需要通知
            Iterator<Integer> iterator = allManagers.iterator();
            while (iterator.hasNext()){
                Integer magId = iterator.next();
                Session session = getSession(String.valueOf(magId) + "_header");
                if(session!=null){
                    System.out.println("通知管理员："+magId);
                    sendMessage(message,session);
                }
            }
            relationService.applyToJoinGroup(message.getFromId(),message.getToId(),message.getContent());
        }

        //记录日志
        getOprLogService().addOprLog(oprLog);

    }

    //发布朋友圈或发布说说
    public void publishDiscussPost(Message message){

    }

}
