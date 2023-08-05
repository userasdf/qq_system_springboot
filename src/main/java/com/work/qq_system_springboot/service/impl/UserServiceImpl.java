package com.work.qq_system_springboot.service.impl;

import com.work.qq_system_springboot.entity.*;
import com.work.qq_system_springboot.mapper.LoginTicketMapper;
import com.work.qq_system_springboot.mapper.UserMapper;
import com.work.qq_system_springboot.service.UserService;
import com.work.qq_system_springboot.tools.MailClient;
import com.work.qq_system_springboot.tools.QQSystemConstant;
import com.work.qq_system_springboot.tools.QQSystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserServiceImpl implements UserService,QQSystemConstant{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    //域名
    @Value("${community.path.domain}")
    private String domain;

    //项目名
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public List<User> findLikeUsername(String username,int ignoreId) {
        return userMapper.findLikeUsername(username,ignoreId);
    }

    @Override
    public int logout(String ticket) {
        return loginTicketMapper.updateStatus(ticket,1);
    }

    @Override
    public User findById(int id) {
        return userMapper.findById(id);
    }

    @Override
    public List<Link> getLinkListByUserId(int userId,String key) {
        return userMapper.getLinkListByUserId(userId,key);
    }

    @Override
    public Link getLinkById(int id) {
        return userMapper.getLinkById(id);
    }


    //用户注册
    @Override
    public Map<String, Object> register(User user) {
        Map<String,Object> map = new HashMap<>();

        //注册前检查
        User u = userMapper.findByUsername(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","用户名已经存在！");
            return map;
        }
        u = userMapper.findByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","该邮箱已经被使用！");
            return map;
        }

        //执行注册操作
        user.setSalt(QQSystemUtil.generateUUID().substring(0,5));
        user.setPassword(QQSystemUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);//是否是管理员
        user.setStatus(0);//该账号是否激活
        user.setActivationCode(QQSystemUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        user.setPersonalLabel("这位用户还没有设置个性标签！");
        //添加一条记录
        userMapper.insert(user);

        //使用模板引擎向发送邮件
        Context context = new Context();
        context.setVariable("username",user.getUsername());
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();

        System.out.println(url);

        context.setVariable("url",url);
        String text = templateEngine.process("mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账号",text);
        return map;
    }

    @Override
    public int activation(int id, String code) {
        User user = userMapper.findById(id);
        if(user==null)
            return ACTIVATION_FAIL;//激活失败
        if(user.getStatus()==1){//重复激活
            return ACTIVATION_REPEAT;
        }else if(!user.getActivationCode().equals(code)){
            return ACTIVATION_FAIL;//激活码不正确
        }else{
            userMapper.updateStatus(id);
            return ACTIVATION_SUCCESS;//激活成功
        }
    }

    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();


        User user = userMapper.findByUsername(username);
        System.out.println("username:"+username);
        //验证账号
        if(user==null){
            map.put("usernameMsg","该账号不存在！");
            System.out.println("该账号不存在！");
            return map;
        }
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活！");
            System.out.println("该账号未激活！");
            return map;
        }
        //验证密码
        password = QQSystemUtil.md5(password+user.getSalt());
        if(!password.equals(user.getPassword())){
            map.put("passwordMsg","密码错误！");
            System.out.println("密码错误！");
            return map;
        }

        //用户登录成功，生成用户登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(QQSystemUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));



        //把登录凭证保存到数据库
        loginTicketMapper.insertLoginTicket(loginTicket);
        //把登录凭证发送到客户端
        map.put("ticket",loginTicket.getTicket());

        System.out.println(map);

        return map;
    }

    @Override
    public LoginTicket findLoginTiekctByTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    @Override
    public int updateHeaderUrl(String headerUrl, int id) {
        return userMapper.updateHeaderUrl(headerUrl,id);
    }

    @Override
    public int updatePwd(int uid, String pwd) {
        return userMapper.updatePwd(uid,pwd);
    }

    @Override
    public int updateInfo(User user) {
        return userMapper.updateInfo(user);
    }

    @Override
    public List<DiscussPost> getFriendCircleInfo(int userId) {
        return userMapper.getFriendCircleInfo(userId);
    }

    @Override
    public List<Comment> getCommentByDiscussPostId(int id) {
        return userMapper.getCommentByDiscussPostId(id);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return userMapper.findDiscussPostById(id);
    }

    @Override
    public int addDiscussPost(int userId, String content, Date createTime) {
        return userMapper.addDiscussPost(userId,content,createTime);
    }

    @Override
    public int clickLike(int fromId, int entityType, int entityId, Date date) {
        int state = userMapper.isClickLike(fromId,entityType,entityId);
        if(state==0)//还未点赞
            return userMapper.clickLike(fromId,entityType,entityId,new Date());
        //已经点过赞，则取消点赞
        userMapper.cancelClickLike(fromId,entityType,entityId);
        return 0;
    }

    @Override
    public int isClickLike(int fromId, int entityType, int entityId) {
        return userMapper.isClickLike(fromId,entityType,entityId);
    }

    @Override
    public int getLikeCount(int entityType, int entityId) {
        return userMapper.getLikeCount(entityType,entityId);
    }

    @Override
    public void deleteLinkById(int id) {
        userMapper.deleteLinkById(id);
    }

    @Override
    public int createLink(int userId, String key, String value,Date date,int orderColumn) {
        return userMapper.createLink(userId,key,value,date,orderColumn);
    }

    @Override
    public void updateLink(Link link) {
        userMapper.updateLinkById(link);
    }

    @Override
    public Link getLeftLinkByOrderColumn(int orderColumn,int userId) {
        return userMapper.getLeftLinkByOrderColumn(orderColumn,userId);
    }

    @Override
    public Link getRightLinkByOrderColumn(int orderColumn,int userId) {
        return userMapper.getRightLinkByOrderColumn(orderColumn,userId);
    }
}
