package com.work.qq_system_springboot.mapper;

import com.work.qq_system_springboot.entity.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CommentMapper {

    //添加一条评论
    @Insert("insert into comment(user_id,entity_id,target_id,content,status,create_time) " +
            "values(#{userId},#{entityId},#{targetId},#{content},#{status},#{createTime})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    public int insertComment(Comment comment);

    //根据id获取评论
    @Select("select * from comment " +
            "where id=#{id}")
    public Comment getCommentById(int id);
}
