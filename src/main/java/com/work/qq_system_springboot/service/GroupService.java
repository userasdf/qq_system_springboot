package com.work.qq_system_springboot.service;

import com.work.qq_system_springboot.entity.Group;

import java.util.List;

public interface GroupService {

    //根据id查找群
    Group findById(int id);

    //根据群名称查找群
    List<Group> findByGroupName(String groupName);

    //添加群聊
    int addGroup(String groupName,int createrId);


    //获取群成员等级
    int getGroupUserLevel(int groupId,int userId);

    //更新群成员等级
    int updateGroupUserLevel(int level,int groupId,int userId);


}
