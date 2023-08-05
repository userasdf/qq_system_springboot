package com.work.qq_system_springboot.service.impl;

import com.work.qq_system_springboot.entity.Group;
import com.work.qq_system_springboot.entity.Relation;
import com.work.qq_system_springboot.mapper.GroupMapper;
import com.work.qq_system_springboot.mapper.RelationMapper;
import com.work.qq_system_springboot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class GroupServiceImpl implements GroupService{

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private RelationMapper relationMapper;

    @Override
    public Group findById(int id) {
        return groupMapper.findById(id);
    }

    @Override
    public List<Group> findByGroupName(String groupName) {
        return groupMapper.findByGroupName(groupName);
    }

    @Override
    public int addGroup(String  groupName, int createrId) {
        Group group = new Group();
        group.setGroupName(groupName);
        group.setCreateUser(createrId);
        group.setCreateTime(new Date());
        group.setHeaderUrl(
                String.format("http://images.nowcoder.com/head/%dt.png",
                        new Random().nextInt(1000)));
        groupMapper.addGroup(group);//群组表中添加一条记录

        Relation relation = new Relation();
        relation.setFromId(group.getId());//群组id
        relation.setToId(createrId);//创建者id
        relation.setIsGroup(1);//是否为群关系
        relation.setLevel(2);//群主级别
        relation.setStatus(1);//是否为群成员
        relationMapper.addFriend(relation);
        return 1;
    }

    @Override
    public int getGroupUserLevel(int groupId, int userId) {
        return groupMapper.getGroupUserLevel(groupId,userId);
    }

    @Override
    public int updateGroupUserLevel(int level, int groupId, int userId) {
        return groupMapper.updateMemberLevel(level,groupId,userId);
    }
}
