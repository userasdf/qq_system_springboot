package com.work.qq_system_springboot.controller;


import com.work.qq_system_springboot.entity.*;
import com.work.qq_system_springboot.event.EventProducer;
import com.work.qq_system_springboot.service.*;
import com.work.qq_system_springboot.tools.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController implements QQSystemConstant{

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private RelationService relationService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private OprLogService oprLogService;

    @Value("${community.file_system.path}")
    private String fileRoot;

    @Value("${community.header.path}")
    private String imagePath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    //模糊查询用户或群聊
    @RequestMapping("/vagueSearchUserOrGroup")
    public String likeUsername(String name, Model model, HttpServletRequest request) throws IOException {

        User user = hostHolder.get();

        List<Map<String,Object>> voUserList = new ArrayList<>();
        List<Map<String,Object>> voGroupList = new ArrayList<>();
        List<User> userList = userService.findLikeUsername(name,user.getId());
        List<Group> groupList = groupService.findByGroupName(name);
        Map<String,Object> map = null;

        for (User temp : userList) {
            Relation relation = relationService.selectRelationByTwoUser(user.getId(), temp.getId());
            map = new HashMap<>();
            if(relation==null){//两者属于陌生关系
                map.put("relation","陌生");
            }else if(relation.getStatus()==1){
                map.put("relation","好友");
            }else{
                map.put("relation","待验证");
            }
            map.put("user",temp);
            voUserList.add(map);
        }
        Iterator<Group> iterator = groupList.iterator();
        while (iterator.hasNext()){
            Group group = iterator.next();
            User createUser = userService.findById(group.getCreateUser());
            Relation relation = relationService.selectRelationByUserAndGroup(user.getId(), group.getId());
            map = new HashMap<>();
            if(relation==null){
                map.put("relation","陌生");
            }else if(relation.getStatus()==1){
                map.put("relation","群成员");
            }else{
                map.put("relation","待验证");
            }
            map.put("group",group);
            map.put("createUser",createUser);
            voGroupList.add(map);
        }

        model.addAttribute("voUserList",voUserList);
        model.addAttribute("voGroupList",voGroupList);
        model.addAttribute("userSize",voUserList.size());
        model.addAttribute("groupSize",voGroupList.size());

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("根据关键词\""+name+"\"进行了一次模糊查询");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "site/search";
    }



    //获取账号设置页面
    @RequestMapping("/getSettingPage")
    public String getSettingPage(HttpServletRequest request) throws IOException {




        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("进入了账号设置页面");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "site/setting";
    }

    //上传用户头像
    @RequestMapping(value = "/setHeader",method = RequestMethod.POST)
    public String setHeader(MultipartFile headerImage,HttpServletRequest request) throws IOException {
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.indexOf("."));
        filename = QQSystemUtil.generateUUID()+suffix;
        String path = this.imagePath+"/"+filename;
        try {
            headerImage.transferTo(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取当前用户(本地线程获取用户)
        User user = hostHolder.get();
        //用户头像路径
        String headerUrl = domain+"/qq_system/user/image/"+filename;
        userService.updateHeaderUrl(headerUrl,user.getId());




        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("更新了头像");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "redirect:/index";
    }

    //返回用户头像
    @RequestMapping("/image/{fileName}")
    public void getHeaderImage(@PathVariable("fileName") String header,HttpServletResponse response){
        try (
                ServletOutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(imagePath+"/"+header);
        ){
            String suffix = header.substring(header.indexOf("."));
            response.setContentType("image/"+suffix);

            byte []buffer = new byte[12];
            int b = 0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //更新密码
    @RequestMapping("/updatePwd")
    public String updatePwd(String oldPwd, String newPwd, Model model,HttpServletRequest request) throws IOException {
        User user = hostHolder.get();
        oldPwd = QQSystemUtil.md5(oldPwd+user.getSalt());
        if(!oldPwd.equals(user.getPassword())){
            model.addAttribute("msg","密码不正确！");
            return "site/setting";
        }
        newPwd = QQSystemUtil.md5(newPwd+user.getSalt());
        userService.updatePwd(user.getId(),newPwd);


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("修改了密码");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "redirect:/logout";
    }

    //进入忘记密码页面
    @RequestMapping("/getForgetPwdPage")
    public String getForgetPwdPage(){
        return "forget_pwd/forget_pwd";
    }

    //发送邮箱验证码
    @RequestMapping("/sendEmailCode")
    @ResponseBody
    public String sendEmailCode(String email,HttpServletRequest request) throws UnsupportedEncodingException {
        //处理中文乱码
        email = URLDecoder.decode(email,"utf8");
        User byEmail = userService.findByEmail(email);
        if(byEmail==null){
            System.out.println("该邮箱未绑定，密码无法找回!");
            return "该邮箱未绑定，密码无法找回!";
        }
        //使用模板引擎向发送邮件
        Context context = new Context();
        context.setVariable("username",byEmail.getUsername());
        //验证码
        String varifyCode = QQSystemUtil.generateUUID().substring(0,6);
        request.getSession().setAttribute("varifyCode",varifyCode);
        context.setVariable("varifyCode",varifyCode);
        String text = templateEngine.process("mail/forget", context);
        mailClient.sendMail(email,"验证码",text);
        return "success!";
    }

    //个人主页
    @RequestMapping("/profile")
    public String profile(String id,Model model,HttpServletRequest request) throws IOException {

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("查看个人信息");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        if(id!=null){
            User user = userService.findById(Integer.parseInt(id));
            model.addAttribute("tempUser",user);
            oprLog.setInfo("查看用户:\""+user.getUsername()+"\"的信息");
        }
        oprLogService.addOprLog(oprLog);

        return "site/user_info";
    }

    //更新用户个人信息
    @RequestMapping("/updateInfo")
    public String updateInfo(User user,HttpServletRequest request) throws IOException {


        user.setId(hostHolder.get().getId());
        userService.updateInfo(user);



        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("更新了用户个人信息");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "redirect:/user/profile";
    }

    //获取朋友圈页面
    @RequestMapping("/getFriendCirclePage")
    public String getFriendCirclePage(HttpServletRequest request) throws IOException {


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("打开了朋友圈");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "site/friend-circle";
    }

    //获取好友朋友圈信息
    @RequestMapping("/getFriendCircleInfo")
    @ResponseBody
    public String getFriendCircleInfo() throws IOException {

        //获取当前登录用户
        User user = hostHolder.get();
        List<DiscussPost> friendCircleInfo = userService.getFriendCircleInfo(user.getId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String res = "";

        System.out.println("********-----------------**********");
        //遍历当前用户的朋友圈
        for (DiscussPost temp : friendCircleInfo) {


            //获取当前帖子的作者
            User u = userService.findById(temp.getUserId());
            String createPostTime = temp.getCreateTime()!=null?dateFormat.format(temp.getCreateTime()):"";
            //判断是否给当前帖子点赞
            int isClickLike = userService.isClickLike(user.getId(),ENTITY_TYPE_DISCUSSPOST,temp.getId());
            //获取当前帖子的点赞数量
            int likeCount = userService.getLikeCount(ENTITY_TYPE_DISCUSSPOST,temp.getId());
            //作者信息：用户名、头像地址、帖子内容、发布时间、帖子id、是否点赞该帖子、点赞数量
            String authorInfo = u.getUsername()+"*,*"+u.getHeaderUrl()+
                    "*,*"+temp.getContent()+"*,*"+createPostTime+"*,*"+temp.getId()+"*,*"+isClickLike+"*,*"+likeCount+"*,*"+u.getId();
            //获取当前帖子的评论
            List<Comment> commentList = userService.getCommentByDiscussPostId(temp.getId());
            String commentStr = "";
            for(Comment comment:commentList){
                User fromUser = userService.findById(comment.getUserId());
                User targetUser = userService.findById(comment.getTargetId());
                String target_name = targetUser==null?"*null*":targetUser.getUsername();
                String target_id = targetUser==null?"*null*":targetUser.getId()+"";
                String createTime = comment.getCreateTime()!=null?dateFormat.format(comment.getCreateTime()):"";
                //判断是否给当前评论点赞
                int isClickLikeComment = userService.isClickLike(user.getId(),ENTITY_TYPE_COMMENT,comment.getId());
                //获取当前评论的点赞数
                int commentLikeCount = userService.getLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentStr += "*|*"+fromUser.getId()+"*,*"+fromUser.getUsername()+"*,*"+target_name+"*,*"
                        +comment.getContent()+"*,*"+createTime+"*,*"+isClickLikeComment+"*,*"+commentLikeCount+"*,*"+comment.getId()+"*,*"+target_id;
            }
            if(commentStr.length()>0){
                authorInfo += commentStr;
            }
            res += authorInfo+"*;*";
        }
        if(res.length()>0)
            res = res.substring(0,res.length()-3);
        System.out.println("********++++++++++++++++++**********");



        return res;
    }

    //发布说说
    @RequestMapping(value = "/publishDiscussPost",method = RequestMethod.POST)
    @ResponseBody
    public String publishDiscussPost(String content,HttpServletRequest request) throws IOException {
        User user = hostHolder.get();
        userService.addDiscussPost(user.getId(),content,new Date());




        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("发布了一条说说：\""+content+"\"");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "success!";
    }

    //评论说说
    @RequestMapping("/commentDiscussPost")
    @ResponseBody
    public String commentDiscussPost(int entityId,int targetId
            ,String content,String topic,int commentId,HttpServletRequest request) throws IOException {
        User user = hostHolder.get();
        //当前用户在某条帖子下的评论（可能是对某一个用户的回复，此时targetId，commentId不为0）
        Comment comment = new Comment()
                .setUserId(user.getId())
                .setEntityId(entityId)
                .setTargetId(targetId)
                .setContent(content)
                .setStatus(0)
                .setCreateTime(new Date());
        commentService.addComment(comment);


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("评论了某说说或回复了某评论：\""+content+"\"");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        //获取当前帖子的作者id
        int discussPostUserId = userService.findDiscussPostById(entityId).getUserId();

        //通知帖子作者，有人对帖子进行了评论
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(user.getId())//某一个人
                .setTargetId(discussPostUserId)//对某一个用户
                .setComment(content)//进行了评论
                .setEntityType(ENTITY_TYPE_DISCUSSPOST)//是对该用户的某一个帖子进行评论
                .setEntityId(entityId);//帖子id
        //生产者生产事件
        eventProducer.produceEvent(event);

        //如果当前用户是在当前帖子下，对某一个用户的回复，需要通知该用户,有人对该用户的该评论进行了回复
        if(targetId!=0){
            event = new Event()
                    .setTopic(TOPIC_REPLY)
                    .setUserId(user.getId())//某一个人
                    .setTargetId(targetId)//对某一个用户
                    .setComment(content)//进行了回复
                    .setEntityType(ENTITY_TYPE_COMMENT)//是对该用户在某一个帖子的评论的回复
                    .setEntityId(commentId);//评论id
            //生产者生产事件
            eventProducer.produceEvent(event);
        }



        return topic+"成功！";
    }

    //点赞
    @RequestMapping("/like")
    @ResponseBody
    public String like(int entityType,int entityId,HttpServletRequest request) throws IOException {
        User user = hostHolder.get();
        int stats = userService.clickLike(user.getId(),entityType,entityId,new Date());


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("点赞了评论或说说");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        //获取当前评论的作者
        int targetId;
        if(entityType==ENTITY_TYPE_DISCUSSPOST)
            targetId = userService.findDiscussPostById(entityId).getUserId();
        else
            targetId = commentService.getCommentById(entityId).getUserId();

        //生产者生产事件
        Event event = new Event()
                .setTopic(TOPIC_LIKE)
                .setUserId(user.getId())
                .setTargetId(targetId)
                .setComment(stats+"")
                .setEntityType(entityType)
                .setEntityId(entityId);
        eventProducer.produceEvent(event);





        if(stats==1)
            return "点赞成功！";
        else
            return "取消点赞成功！";


    }

    //获取系统通知页面
    @RequestMapping("/getSystemAdvicePage")
    public String getSystemAdvicePage(HttpServletRequest request) throws IOException {


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("进入了系统通知页面");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "site/system_advice";
    }

    //进入操作日志页面
    @RequestMapping("/getOprLogs")
    public String getOprLogs(HttpServletRequest request,Model model,Page page){

        User user = hostHolder.get();
        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("查看当前用户第"+page.getCurrent()+"页日志");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        page.setRows(oprLogService.getOprLogsCountByUserId(user.getId()));
        page.setPath("/user/getOprLogs");

        List<OprLog> oprLogList = oprLogService.getOprLogsByUserId(user.getId(),page.getOffset(),page.getLimit());
        model.addAttribute("oprLogList",oprLogList);
        model.addAttribute("tempUserType","tempUserType");//获取的日志类型为当前用户
        model.addAttribute("rows",page.getRows());
        model.addAttribute("user",user);
        return "site/opr_log";
    }

    //根据用户名查询日志
    @RequestMapping("/getOprLogsByUsername")
    public String getOprLogsByUsername(String userName,Model model, Page page,HttpServletRequest request) throws UnsupportedEncodingException {
        User user = hostHolder.get();
        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("根据用户名\""+userName+"\"查看第"+page.getCurrent()+"页日志");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);



        //处理中文乱码，获取文件夹名字
        userName = URLDecoder.decode(userName,"utf8");

        page.setRows(oprLogService.getOprLogsCountByUserName(userName));
        page.setPath("/user/getOprLogsByUsername?userName="+userName);
        List<OprLog> oprLogList = oprLogService.getOprLogsByUserName(userName,page.getOffset(),page.getLimit());
        model.addAttribute("oprLogList",oprLogList);
        model.addAttribute("byUserNameType","byUserNameType");//获取的日志类型为根据用户名查询
        model.addAttribute("rows",page.getRows());
        model.addAttribute("userName",userName);
        model.addAttribute("user",user);
        return "site/opr_log";
    }

    //根据ip查询日志
    @RequestMapping("/getOprLogsByIpAddress")
    public String getOprLogsByIpAddress(String ipAddress,Model model, Page page,HttpServletRequest request) throws UnsupportedEncodingException {
        User user = hostHolder.get();

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("根据ip地址\""+ipAddress+"\"查看第"+page.getCurrent()+"页日志");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);


        //处理中文乱码，获取文件夹名字
        ipAddress = URLDecoder.decode(ipAddress,"utf8");

        page.setRows(oprLogService.getOprLogsCountByIpAddress(ipAddress));
        page.setPath("/user/getOprLogsByIpAddress?ipAddress="+ipAddress);
        List<OprLog> oprLogList = oprLogService.getOprLogsByIpAddress(ipAddress,page.getOffset(),page.getLimit());
        model.addAttribute("oprLogList",oprLogList);
        model.addAttribute("byIpAddressType","byIpAddressType");//获取的日志类型为根据用户名查询
        model.addAttribute("rows",page.getRows());
        model.addAttribute("ipAddress",ipAddress);
        model.addAttribute("user",user);
        return "site/opr_log";
    }

    //获取所有用户的操作日志
    @RequestMapping("/getAllOprLogs")
    public String getAllOprLogs(Model model,Page page,HttpServletRequest request){
        User user = hostHolder.get();

        if(user.getType()!=1)
            return "redirect:/user/getOprLogs";

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("查看所有用户第"+page.getCurrent()+"页日志");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        page.setRows(oprLogService.getAllOprLogsCount(user.getId()));
        page.setPath("/user/getAllOprLogs");

        List<OprLog> oprLogList = oprLogService.getAllOprLogs(user.getId(),page.getOffset(),page.getLimit());
        model.addAttribute("oprLogList",oprLogList);
        model.addAttribute("otherType","otherType");//获取的日志类型为其他所有用户
        model.addAttribute("rows",page.getRows());
        model.addAttribute("user",user);

        return "site/opr_log";
    }

    //个人链接
    @RequestMapping("/personalLink")
    public String personalLink(String key,Model model,HttpServletRequest request){
        if(key==null){
            key = "";//key为空，则代表查询所有的记录
        }

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("【个人链接】根据key"+key+"获取个人链接");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        User user = hostHolder.get();
        List<Link> linkList = userService.getLinkListByUserId(user.getId(),key);
        model.addAttribute("linkList",linkList);
        model.addAttribute("linkListSize",linkList.size());
        return "site/personal_link_list";
    }

    //根据id删除链接
    @RequestMapping("/deleteLink")
    @ResponseBody
    public void deleteLink(int id,HttpServletRequest request){

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("【个人链接】根据id"+id+"删除个人链接");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        userService.deleteLinkById(id);
    }


    //发布链接
    @PostMapping("/createLink")
    @ResponseBody
    public void createLink(String key,String value,HttpServletRequest request){

        //解析url
        value = QQSystemUtil.getUrlFromText(value);

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("【个人链接】新建链接{key:"+key+",value:"+value+"}");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);


        //确定链接的order_column
        int orderColumn = 1;
        //获取当前用户的最新链接
        List<Link> linkList = userService.getLinkListByUserId(hostHolder.get().getId(),"");
        if(linkList.size()!=0){
            orderColumn = linkList.get(0).getOrderColumn()+1;
        }
        System.out.println("order:"+orderColumn);
        int cnt = userService.createLink(hostHolder.get().getId(), key, value, new Date(), orderColumn);
        System.out.println("cnt:"+cnt);
    }

    @RequestMapping("/updateLink")
    @ResponseBody
    public void updateLink(String key,String value,int linkId,HttpServletRequest request){
        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("【个人链接】更新链接");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        Link link = userService.getLinkById(linkId);
        link.setKey(key);
        link.setValue(value);
        userService.updateLink(link);
    }

    @RequestMapping("/moveLink")
    @ResponseBody
    public String moveLink(String id,String dir,HttpServletRequest request){

        Link linkById = userService.getLinkById(Integer.parseInt(id));

        Link tempLink = null;
        if(dir.equals("down")){
            tempLink = userService.getLeftLinkByOrderColumn(linkById.getOrderColumn(),hostHolder.get().getId());
        }else{
            tempLink = userService.getRightLinkByOrderColumn(linkById.getOrderColumn(),hostHolder.get().getId());
        }


        if(tempLink==null){
            return "-1";//没有邻居，无法移动
        }


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("【个人链接】移动链接");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);


        //更新链接的orderColumn
        int temp = tempLink.getOrderColumn();
        tempLink.setOrderColumn(linkById.getOrderColumn());
        linkById.setOrderColumn(temp);
        userService.updateLink(linkById);
        userService.updateLink(tempLink);

        return ""+tempLink.getId();
    }

    //更改链接背景颜色
    @RequestMapping("/changeBackColor")
    @ResponseBody
    public void changeBackColor(int id,String color,String keyColor){
        Link linkById = userService.getLinkById(id);
        linkById.setBackColor(color);
        linkById.setKeyColor(keyColor);
        userService.updateLink(linkById);
    }

    @RequestMapping("/knowledgeGraph")
    public String knowledgeGraph(HttpServletRequest request){

        int userId = hostHolder.get().getId();
        String userName = hostHolder.get().getUsername();

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(userId);
        oprLog.setUserName(userName);
        oprLog.setInfo("【知识图谱】访问知识图谱");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);


        String ticket = CookieUtil.getValue(request, "ticket");
//        return "redirect:http://81.68.198.238:8080?ticket="+ticket;
        return "redirect:http://www.sqdrz.asia:8081?ticket="+ticket;
    }

}