package com.work.qq_system_springboot.mapper;

import com.work.qq_system_springboot.entity.DiscussPost;
import com.work.qq_system_springboot.entity.Relation;
import com.work.qq_system_springboot.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RelationMapper {

    //请求添加好友
    @Insert("insert into relation(from_id,to_id,val_msg,status,create_time,is_group,level) " +
            "values(#{fromId},#{toId},#{valMsg},#{status},#{createTime},#{isGroup},#{level})")
    public int addFriend(Relation relation);

    //查询两个用户之间的关系
    @Select("select * from relation where from_id=#{fromId} and to_id=#{toId} and is_group=0")
    public Relation selectRelationByTwoUser(@Param("fromId")int fromId,@Param("toId")int toId);

    //查询某一个用户与一个群的关系
    @Select("select * from relation where from_id=#{groupId} and to_id=#{userId} and is_group=1")
    public Relation selectRelationByUserAndGroup(@Param("userId")int userId,@Param("groupId")int groupId);

    //获取所有验证关系
    @Select("SELECT user.`id`,user.`username`,user.`password`, " +
            "user.`salt`,user.`email`,user.`type`,user.`status`, " +
            "user.`activation_code`,user.`header_url`,user.`create_time`, " +
            "user.`personal_label`,val_msg FROM `user`,`relation` " +
            "WHERE from_id=user.`id` " +
            "AND to_id=#{toId} " +
            "and relation.status=2 " +
            "and relation.is_group=0")
    public List<User> getAllValFriend(@Param("toId") int toId);


    //获取当前群聊的所有用户进群申请
    @Select("SELECT user.`id`,user.`username`,user.`password`, " +
            "user.`salt`,user.`email`,user.`type`,user.`status`, " +
            "user.`activation_code`,user.`header_url`,user.`create_time`, " +
            "user.`personal_label`,val_msg FROM `user`,`relation` " +
            "WHERE to_id=user.`id` " +
            "AND from_id=#{groupId} " +
            "and relation.status=2 " +
            "and relation.is_group=1")
    public List<User> getAllJoinGroupApply(@Param("groupId") int groupId);


    //接受好友申请
    @Update("UPDATE relation SET status=1 " +
            "WHERE from_id=#{fromId} AND to_id=#{toId} and is_group=0")
    public int acceptFriend(@Param("fromId")int fromId,@Param("toId")int toId);


    //接受进群申请
    @Update("update relation set status=1 " +
            "where from_id=#{groupId} " +
            "and to_id=#{userId} " +
            "and is_group=1")
    public int acceptJoinGroupApply(@Param("userId")int userId,@Param("groupId")int groupId);


    //删除某一条记录
    @Delete("delete from relation " +
            "where from_id=#{fromId} " +
            "and to_id=#{toId} " +
            "and is_group=#{isGroup}")
    public int delete(@Param("fromId")int fromId,@Param("toId")int toId,@Param("isGroup")int isGroup);



    //获取所有的好友
    @Select("SELECT user.`id`,user.`username`,user.`password`, " +
            "user.`salt`,user.`email`,user.`type`,user.`status`, " +
            "user.`activation_code`,user.`header_url`,user.`create_time`, " +
            "user.`personal_label`,val_msg FROM `user`,`relation` " +
            "WHERE to_id=user.`id` " +
            "AND from_id=#{id} " +
            "AND relation.status=1 " +
            "and relation.is_group=0")
    public List<User> getAllFriend(int id);

    //获取当前用户所有已加入的群
    @Select("select from_id from relation " +
            "where to_id=#{userId} " +
            "and is_group=1 " +
            "and status=1 " +
            "and level>=#{level}")
    public List<Integer> getAllEnterdGroup(@Param("userId") int userId,@Param("level") int level);

    //获取当前群的所有用户
    @Select("select to_id from relation " +
            "where from_id=#{groupId} " +
            "and is_group=1 " +
            "and status=1 " +
            "and level>=#{level}")
    public List<Integer> getAllUserByGroupId(@Param("groupId") int groupId,@Param("level") int level);

}
