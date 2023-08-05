package com.work.qq_system_springboot.service;

import com.work.qq_system_springboot.entity.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserService {

    //获取所有用户
    public List<User> findAll();

    //根据用户昵称查询用户
    public User findByUsername(String username);

    //根据邮箱查询用户
    public User findByEmail(String email);

    //模糊查询用户
    public List<User> findLikeUsername(String username,int ignoreId);

    //用户登出
    public int logout(String ticket);

    //根据用户id查询用户
    public User findById(int id);

    //获取个人链接
    public List<Link> getLinkListByUserId(int userId,String key);


    //根据id查询链接
    public Link getLinkById(int id);

    //注册用户
    public Map<String,Object> register(User user);

    //激活用户
    public int activation(int id,String code);

    //用户登录
    public Map<String,Object> login(String username,String password,int expiredSeconds);


    //获取用户登录凭证
    public LoginTicket findLoginTiekctByTicket(String ticket);

    //更新用户头像
    public int updateHeaderUrl(String headerUrl,int id);

    //修改用户密码
    public int updatePwd(int uid,String pwd);

    //更新用户个人信息
    public int updateInfo(User user);

    //获取当前用户的朋友圈信息
    public List<DiscussPost> getFriendCircleInfo(int userId);

    //获取当前帖子的所有评论
    public List<Comment> getCommentByDiscussPostId(int id);

    //根据帖子id获取用户
    public DiscussPost findDiscussPostById(int id);

    //发布一条说说
    public int addDiscussPost(int userId, String content, Date createTime);

    //点赞
    public int clickLike(int fromId,int entityType,int entityId,Date date);

    //查看是否点赞
    public int isClickLike(int fromId,int entityType,int entityId);

    //查看点赞数量
    public int getLikeCount(int entityType,int entityId);

    void deleteLinkById(int id);

    int createLink(int userId, String key, String value,Date date,int orderColumn);

    void updateLink(Link link);


    //查询左邻链接
    Link getLeftLinkByOrderColumn(int orderColumn,int userId);

    //查询右邻连接
    Link getRightLinkByOrderColumn(int orderColumn,int userId);
}
