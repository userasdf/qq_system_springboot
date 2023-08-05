package com.work.qq_system_springboot.service.impl;

import com.work.qq_system_springboot.entity.Relation;
import com.work.qq_system_springboot.entity.User;
import com.work.qq_system_springboot.mapper.RelationMapper;
import com.work.qq_system_springboot.service.RelationService;
import com.work.qq_system_springboot.tools.QQSystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelationServiceImpl implements RelationService,QQSystemConstant{

    @Autowired
    private RelationMapper relationMapper;

    @Override
    public Map<String,Object> addFriend(int fromId, int toId, String valMsg) {
        Map<String,Object> map = new HashMap<>();
        Relation relation = new Relation(fromId,toId,valMsg,HAVE_NOT_VARIFY,new Date(),0,0);
        relationMapper.addFriend(relation);
        return map;
    }

    //申请入群
    @Override
    public int applyToJoinGroup(int userId, int groupId, String valMsg) {
        Relation relation = new Relation(groupId,userId,valMsg,HAVE_NOT_VARIFY,new Date(),1,0);
        return relationMapper.addFriend(relation);
    }

    @Override
    public List<User> getAllVarifyFriend(int toId) {
        return relationMapper.getAllValFriend(toId);
    }

    @Override
    public List<User> getAllJoinGroupApply(int groupId) {
        return relationMapper.getAllJoinGroupApply(groupId);
    }

    @Override
    public List<User> getAllFriend(int id) {
        return relationMapper.getAllFriend(id);
    }

    @Override
    public List<Integer> getAllEnteredGroup(int userId,int level) {
        return relationMapper.getAllEnterdGroup(userId,level);
    }

    @Override
    public Relation selectRelationByTwoUser(int fromId, int toId) {
        return relationMapper.selectRelationByTwoUser(fromId,toId);
    }

    @Override
    public Relation selectRelationByUserAndGroup(int userId, int groupId) {
        return relationMapper.selectRelationByUserAndGroup(userId,groupId);
    }

    @Override
    public List<Integer> getAllUserByGroupId(int groupId,int level) {
        return relationMapper.getAllUserByGroupId(groupId,level);
    }

    @Override
    public int acceptFriend(int fromId, int toId) {
        relationMapper.acceptFriend(fromId,toId);
        this.addFriend(toId,fromId,"");
        relationMapper.acceptFriend(toId,fromId);
        return 1;
    }

    @Override
    public int acceptJoinGroupApply(int groupId, int userId) {
        return relationMapper.acceptJoinGroupApply(userId,groupId);
    }

    //拒绝好友申请，相当于删除该记录
    @Override
    public int rejectFriend(int fromId, int toId) {
        return relationMapper.delete(fromId,toId,0);
    }

    //拒绝进群申请
    @Override
    public int rejectJoinGroupApply(int groupId, int userId) {
        return relationMapper.delete(groupId,userId,1);
    }
}
