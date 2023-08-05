package com.work.qq_system_springboot.service;

import com.work.qq_system_springboot.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageService {

    //获取与某一个人的会话
    public List<Message> getMessages(int fromId,int toId);

    //获取与某一个人的最新会话
    public Message getLatestMessages(int fromId,int toId);

    //获取某一个群的最新消息
    public Message getLatestMessagesByGroupId(int groupId);

    //获取某一个群的所有消息
    public List<Message> getMessagesByGroupId(int groupId);

    //获取某一个主题下所有的通知
    public List<Message> getAllAdviceByTopic(int toId,String topic);

    //获取某一个主题下的最新通知
    public Message getLastAdviceByTopic(int toId,String topic);

    //发送消息
    public int sendMessage(Message message);

    //获取某一个人发来的未读消息数量
    public int getUnreadCountBySomeBody(@Param("fromId") int fromId, @Param("toId") int toId);

    //获取所有未读消息数量
    public int getAllUnreadedMsgCount(int userId,String topic);

    //获取所有消息数量
    public int getAllMsgCount(int userId,String topic);

    //将某一个人发来的消息设置为已读的
    public int updateStatus(int fromId,int toId,String topic);
}
