package com.work.qq_system_springboot.service;

import com.work.qq_system_springboot.entity.Relation;
import com.work.qq_system_springboot.entity.User;

import java.util.List;
import java.util.Map;

public interface RelationService {

    //请求添加好友
    public Map<String,Object> addFriend(int fromId, int toId, String valMsg);

    //申请入群
    public int applyToJoinGroup(int userId,int groupId,String valMsg);

    //获取所有验证好友
    public List<User> getAllVarifyFriend(int toId);

    //获取当前群的所有用户进群申请
    public List<User> getAllJoinGroupApply(int groupId);

    //获取所有的好友
    public List<User> getAllFriend(int id);

    //获取当前用户所有已加入的群
    public List<Integer> getAllEnteredGroup(int userId,int level);

    //查询两个用户之间的关系
    public Relation selectRelationByTwoUser(int fromId,int toId);

    //查询用户与群的关系
    public Relation selectRelationByUserAndGroup(int userId,int groupId);

    //获取当前群组的所有用户
    public List<Integer> getAllUserByGroupId(int groupId,int level);

    //接受好友申请
    public int acceptFriend(int fromId,int toId);

    //接受进群申请
    public int acceptJoinGroupApply(int groupId,int userId);

    //拒绝好友申请
    public int rejectFriend(int fromId,int toId);

    //拒绝进群申请
    public int rejectJoinGroupApply(int groupId,int userId);
}
