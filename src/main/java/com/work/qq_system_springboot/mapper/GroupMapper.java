package com.work.qq_system_springboot.mapper;

import com.work.qq_system_springboot.entity.Group;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GroupMapper {

    //根据id查找群
    @Select("select * from `group` where `id`=#{id}")
    public Group findById(int id);

    //根据群名称查找群
    @Select("select * from `group` where group_name like CONCAT('%',#{groupName},'%')")
    public List<Group> findByGroupName(String groupName);


    //获取所有已加入的群
    @Select("select from_id from relation " +
            "where to_id=#{toId} " +
            "and is_group=1 " +
            "and status=1")
    public List<Integer> getAllEnterdGroup(int userId);

    //添加群组
    @Insert("insert into `group`(`group_name`,`header_url`,`create_user`,`create_time`) " +
            "values (#{groupName},#{headerUrl},#{createUser},#{createTime})")
    @Options(useGeneratedKeys = true,keyProperty = "id")//添加完记录把自动生成的id返回给group对象
    public int addGroup(Group group);

    //获取群成员等级
    @Select("select level from relation " +
            "where from_id=#{groupId} " +
            "and to_id=#{userId} " +
            "and status=1 " +
            "and is_group=1")
    public int getGroupUserLevel(@Param("groupId")int groupId,@Param("userId")int userId);

    //更新群成员级别
    @Update("update relation " +
            "set level=#{level} " +
            "where from_id=#{groupId} " +
            "and to_id=#{userId} " +
            "and status=1 " +
            "and is_group=1")
    public int updateMemberLevel(@Param("level")int level,@Param("groupId")int groupId,@Param("userId")int userId);

}
