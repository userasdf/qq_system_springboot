package com.work.qq_system_springboot.mapper;

import com.work.qq_system_springboot.entity.Message;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageMapper {

    //新增一条消息
    @Insert("insert into message(from_id,to_id,content,status,create_time,topic,is_group) " +
            "values(#{fromId},#{toId},#{content},#{status},#{createTime},#{topic},#{isGroup})")
    public int addMessage(Message message);

    //获取与某一个人的会话
    @Select("select * from message " +
            "where (from_id=#{fromId} and to_id=#{toId})  " +
            "or (from_id=#{toId} and to_id=#{fromId})  " +
            "and is_group=0 " +
            "order by create_time asc")
    public List<Message> getMessages(@Param("fromId") int fromId, @Param("toId") int toId);

    //获取与某一个人的最新会话
    @Select("select * from message " +
            "where (from_id=#{fromId} and to_id=#{toId})  " +
            "or (from_id=#{toId} and to_id=#{fromId})  " +
            "and is_group=0 " +
            "order by create_time desc " +
            "limit 0,1")
    public Message getLatestMessage(@Param("fromId")int fromId,@Param("toId")int toId);

    //获取某一个主题下所有的通知
    @Select("select * from message " +
            "where to_id=#{toId} " +
            "and topic=#{topic} " +
            "order by create_time desc")
    public List<Message> getAllAdviceByTopic(@Param("toId")int toId,@Param("topic")String topic);

    //获取某一个主题下的最新通知
    @Select("select * from message " +
            "where to_id=#{toId} " +
            "and topic=#{topic} " +
            "order by create_time desc " +
            "limit 0,1")
    public Message getLastAdviceByTopic(@Param("toId")int toId,@Param("topic")String topic);

    //获取与某一个人的未读消息数量
    @Select("select count(id) from message " +
            "where from_id=#{fromId} " +
            "and to_id=#{toId} " +
            "and is_group=0 " +
            "and status=0")
    public int getUnreadCountBySomeBody(@Param("fromId") int fromId,@Param("toId") int toId);

    //获取当前用户的所有消息数量
    @Select("select count(id) from message " +
            "where to_id=#{toId} " +
            "and status=#{status} " +
            "and topic=#{topic} " +
            "and is_group=0")
    public int getAllMsgCount(@Param("toId") int toId,@Param("topic") String topic,@Param("status")int status);

    //将某一个人发来的消息设置为已读状态
    @Update("update message set status=1 " +
            "where status=0 " +
            "and from_id=#{fromId} " +
            "and to_id=#{toId} " +
            "and topic=#{topic} " +
            "and is_group=0")
    public int updateStatus(@Param("fromId") int fromId,@Param("toId") int toId,@Param("topic")String topic);

    @Select("select * from message " +
            "where to_id=#{groupId} " +
            "and is_group=1 " +
            "order by create_time desc " +
            "limit 0,1")
    Message getLatestMessageByGroupId(int groupId);

    @Select("select * from message " +
            "where to_id=#{groupId} " +
            "and is_group=1 " +
            "order by create_time asc")
    List<Message> getMessagesByGroupId(int groupId);
}
