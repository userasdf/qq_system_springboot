package com.work.qq_system_springboot.mapper;

import com.work.qq_system_springboot.entity.Comment;
import com.work.qq_system_springboot.entity.DiscussPost;
import com.work.qq_system_springboot.entity.Link;
import com.work.qq_system_springboot.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Mapper
@Component
public interface UserMapper {

    //查询所有用户
    @Select("select * from user")
    public List<User> findAll();

    //模糊查询用户
    @Select("select * from user where username like CONCAT('%',#{username},'%') " +
            "and id!=#{ignoreId}")
    public List<User> findLikeUsername(@Param("username") String username,@Param("ignoreId") int ignoreId);

    @Select("select * from user where username=#{username}")
    public User findByUsername(String username);
    //根据用户昵称查询用户

    //根据id获取用户
    @Select("select * from user where id=#{id}")
    public User findById(int id);

    //根据邮箱查询用户
    @Select("select * from user where email=#{email}")
    public User findByEmail(String email);

    //添加一条记录
    @Insert("insert into user(username,password,salt,email,type,status,activation_code,header_url,create_time,personal_label) " +
            "values(#{username},#{password},#{salt},#{email},#{type},#{status},#{activationCode},#{headerUrl},#{createTime},#{personalLabel})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    public int insert(User user);

    //用户激活
    @Update("update user set status=1 where id=#{id}")
    public int updateStatus(int id);

    //更新用户头像
    @Update("update user set header_url=#{headerUrl} " +
            "where id=#{id}")
    public int updateHeaderUrl(@Param("headerUrl")String headerUrl,@Param("id")int id);

    //修改用户密码
    @Update("update user set password = #{password} where id=#{uid}")
    public int updatePwd(@Param("uid")int uid,@Param("password")String password);

    //更新用户个人信息
    @Update("update user set age=#{age} " +
            ", school=#{school} " +
            ", address=#{address} " +
            ", email=#{email} " +
            ", personal_label=#{personalLabel} " +
            "where id=#{id}")
    public int updateInfo(User user);

    //获取当前用户的朋友圈信息
    @Select("SELECT * FROM discuss_post " +
            "WHERE user_id = #{userId} OR user_id IN (" +
            " SELECT to_id FROM relation WHERE from_id=#{user_id} and status=1 " +
            ") ORDER BY create_time DESC")
    public List<DiscussPost> getFriendCircleInfo(int userId);

    //获取当前帖子的所有评论
    @Select("SELECT * FROM comment " +
            "WHERE entity_id=#{id} " +
            "order by create_time DESC")
    public List<Comment> getCommentByDiscussPostId(int id);

    //根据帖子id获取帖子
    @Select("select * from discuss_post where id=#{id}")
    public DiscussPost findDiscussPostById(int id);

    //发布一条说说
    @Insert("insert into discuss_post(user_id,content,create_time) values (#{userId},#{content},#{createTime})")
    public int addDiscussPost(@Param("userId")int userId, @Param("content")String content, @Param("createTime")Date createTime);

    //点赞
    @Insert("insert into click_like(from_id,entity_type,entity_id,create_time) " +
            "values(#{fromId},#{entityType},#{entityId},#{createTime})")
    public int clickLike(@Param("fromId")int fromId,@Param("entityType")int entityType
            ,@Param("entityId")int entityId,@Param("createTime")Date date);

    //取消点赞
    @Delete("delete from click_like " +
            "where from_id=#{fromId} " +
            "and entity_type=#{entityType} " +
            "and entity_id=#{entityId}")
    public int cancelClickLike(@Param("fromId")int fromId,@Param("entityType")int entityType,@Param("entityId")int entityId);

    //查看是否点赞
    @Select("select count(id) from click_like " +
            "where from_id=#{fromId} " +
            "and entity_type=#{entityType} " +
            "and entity_id=#{entityId}")
    public int isClickLike(@Param("fromId")int fromId,@Param("entityType")int entityType,@Param("entityId")int entityId);

    //查看点赞数量
    @Select("select count(id) from click_like " +
            "where entity_type=#{entityType} " +
            "and entity_id=#{entityId}")
    public int getLikeCount(@Param("entityType")int entityType,@Param("entityId")int entityId);


    //查询链接
    @Select("select * from personal_link_list where user_id=#{userId} and `key` like CONCAT('%',#{key},'%') order by order_column desc")
    List<Link> getLinkListByUserId(@Param("userId") int userId,@Param("key") String key);

    //根据链接id查询链接
    @Select("select * from personal_link_list where id=#{id}")
    Link getLinkById(int id);

    //查询左邻链接
    @Select("select * from personal_link_list where user_id=#{userId} and order_column<#{orderColumn} order by order_column desc limit 0,1")
    Link getLeftLinkByOrderColumn(@Param("orderColumn") int orderColumn,@Param("userId") int userId);

    //查询右邻链接
    @Select("select * from personal_link_list where user_id=#{userId} and order_column>#{orderColumn} order by order_column limit 0,1")
    Link getRightLinkByOrderColumn(@Param("orderColumn") int orderColumn,@Param("userId") int userId);

    //删除链接
    @Delete("delete from personal_link_list where id=#{id}")
    void deleteLinkById(int id);

    //修改链接
    @Update("update personal_link_list set `key`=#{key},`value`=#{value} " +
            ", order_column=#{orderColumn} ,back_color=#{backColor},key_color=#{keyColor} where id=#{id}")
    void updateLinkById(Link link);

    //创建连接
    @Insert("insert into personal_link_list(`user_id`,`key`,`value`,`create_time`,`order_column`) values (#{userId},#{key},#{value},#{date},#{orderColumn})")
    int createLink(@Param("userId") int userId,@Param("key") String key, @Param("value") String value,@Param("date") Date date,@Param("orderColumn")int orderColumn);


}
