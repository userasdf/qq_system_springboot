package com.work.qq_system_springboot.service.impl;

import com.work.qq_system_springboot.entity.Message;
import com.work.qq_system_springboot.mapper.MessageMapper;
import com.work.qq_system_springboot.service.MessageService;
import com.work.qq_system_springboot.tools.QQSystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService,QQSystemConstant{

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public List<Message> getMessages(int fromId, int toId) {
        return messageMapper.getMessages(fromId,toId);
    }

    @Override
    public Message getLatestMessages(int fromId, int toId) {
        return messageMapper.getLatestMessage(fromId,toId);
    }

    @Override
    public Message getLatestMessagesByGroupId(int groupId) {
        return messageMapper.getLatestMessageByGroupId(groupId);
    }

    @Override
    public List<Message> getMessagesByGroupId(int groupId) {
        return messageMapper.getMessagesByGroupId(groupId);
    }

    @Override
    public List<Message> getAllAdviceByTopic(int toId, String topic) {
        return messageMapper.getAllAdviceByTopic(toId,topic);
    }

    @Override
    public Message getLastAdviceByTopic(int toId, String topic) {
        return messageMapper.getLastAdviceByTopic(toId,topic);
    }

    @Override
    public int sendMessage(Message message) {
        System.out.println(message);
        return messageMapper.addMessage(message);
    }

    @Override
    public int getUnreadCountBySomeBody(int fromId, int toId) {
        return messageMapper.getUnreadCountBySomeBody(fromId,toId);
    }

    //获取所有未读消息数量
    @Override
    public int getAllUnreadedMsgCount(int userId, String topic) {
        return messageMapper.getAllMsgCount(userId,topic,0);
    }

    //获取所有消息数量
    @Override
    public int getAllMsgCount(int userId, String topic) {
        int unreadedCnt = messageMapper.getAllMsgCount(userId,topic,0);
        int readedCnt = messageMapper.getAllMsgCount(userId,topic,1);
        return unreadedCnt+readedCnt;
    }


    @Override
    public int updateStatus(int fromId, int toId,String topic) {
        return messageMapper.updateStatus(fromId,toId,topic);
    }
}
