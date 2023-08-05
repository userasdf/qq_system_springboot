package com.work.qq_system_springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.work.qq_system_springboot.entity.Group;
import com.work.qq_system_springboot.entity.Message;
import com.work.qq_system_springboot.entity.OprLog;
import com.work.qq_system_springboot.entity.User;
import com.work.qq_system_springboot.service.*;
import com.work.qq_system_springboot.tools.FileOperation;
import com.work.qq_system_springboot.tools.HostHolder;
import com.work.qq_system_springboot.tools.QQSystemConstant;
import com.work.qq_system_springboot.tools.QQSystemUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/message")
public class MessageController implements QQSystemConstant{

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private CommentService commentService;

    @Value("${community.send_file.path}")
    private String sendFilePath;


    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private OprLogService oprLogService;

    //获取文件根路径
    @Value("${community.file_system.path}")
    private String fileRoot;

    //日期格式化对象
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //获取发送消息页面
    @RequestMapping("/getMessagePage/{targetId}/{isGroup}")
    public String getMessagePage(@PathVariable("targetId")int targetId,@PathVariable("isGroup")int isGroup,Model model,HttpServletRequest request) throws Exception {

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());


        //返回页面前，把消息读取出来
        List<Message> messageList = null;
        if(isGroup==0){//普通聊天
            User target = userService.findById(targetId);
            messageList = messageService.getMessages(hostHolder.get().getId(), targetId);//获取与好友的所有聊天记录
            model.addAttribute("target",target);//保存好友信息
            //将该好友发送来的消息设置为已读状态
            messageService.updateStatus(targetId, hostHolder.get().getId(),TOPIC_MSG);
            messageService.updateStatus(targetId, hostHolder.get().getId(),TOPIC_FILE);
            oprLog.setInfo("进入了与好友\""+target.getUsername()+"\"的聊天窗口");
        }else{//群聊
            messageList = messageService.getMessagesByGroupId(targetId);//获取当前QQ群的所有聊天记录
            Group target = groupService.findById(targetId);
            model.addAttribute("target",target);//保存群组的信息
            oprLog.setInfo("进入了群聊窗口：\""+target.getGroupName()+"\"");
        }
        Iterator<Message> iterator = messageList.iterator();
        while (iterator.hasNext()){//遍历每一条消息，判断是否为文件消息，如果是则转换content内容
            Message next = iterator.next();
            if(next.getTopic().equals(QQSystemConstant.TOPIC_FILE)){
                byte[] bytes = FileOperation.streamToByteArray(new FileInputStream(this.sendFilePath + "/" + next.getContent()));
                next.setContent(new String(bytes));//将文件名替换为对应的base64数据信息
                System.out.println("change");
            }else{
                System.out.println("continue");
            }
        }
        model.addAttribute("messageList",messageList);
        model.addAttribute("isGroup",isGroup);
        model.addAttribute("user",hostHolder.get());

        //记录操作日志
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "site/letter-detail";
    }



    //获取所有人发来的未读消息数量
    @RequestMapping("/getAllUnreadCount")
    @ResponseBody
    public String getAllUnreadCount(String[] topic){
        User user = hostHolder.get();
        int allUnreadCount = 0;
        for (String t : topic) {
            allUnreadCount += messageService.getAllUnreadedMsgCount(user.getId(),t);
        }
        return allUnreadCount+"";
    }

    //获取所有消息数量（包括未读和已读的）
    @RequestMapping("/getReadAndUnreadMsgCount")
    @ResponseBody
    public String getReadAndUnreadMsgCount(String topic){
        User user = hostHolder.get();
        int res = messageService.getAllMsgCount(user.getId(),topic);
        return res+"";
    }

    //获取当前好友发来的未读消息数量
    @RequestMapping("/getUnreadCountBySomeBody/{fromId}")
    @ResponseBody
    public String getUnreadCountBySomeBody(@PathVariable("fromId")int fromId){
        User user = hostHolder.get();
        int unreadCountBySomeBody = messageService.getUnreadCountBySomeBody(fromId, user.getId());
        return unreadCountBySomeBody+"";
    }

//    //发送消息
//    @RequestMapping("/sendMsg")
//    @ResponseBody
//    public void sendMsg(int toId, String content, HttpServletRequest request) throws IOException {
//
//        //获取当前用户
//        User user = hostHolder.get();
//        if(StringUtils.isBlank(content))
//            content = " ";
//        messageService.sendMessage(user.getId(),toId,content,TOPIC_MSG);
//
//
//        //记录日志
//        String targetName = userService.findById(toId).getUsername();
//        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
//        oprLog.setUserId(hostHolder.get().getId());
//        oprLog.setUserName(hostHolder.get().getUsername());
//        oprLog.setInfo("向\""+targetName+"\"发送了消息：\""+content+"\"");
//        oprLog.setType(LOG_TYPE_INTERACTIVE);
//        oprLogService.addOprLog(oprLog);
//    }

    //获取某一个主题下的所有通知
    @RequestMapping("/getAdviceByTopic")
    public String getAdviceDetail(String topic,Model model){
        //当前登录用户
        User user = hostHolder.get();
        //当前主题下的所有通知
        List<Message> allAdviceByTopic = messageService.getAllAdviceByTopic(user.getId(), topic);
        //更新未读通知为已读
        messageService.updateStatus(SYSTEM_USER_ID, user.getId(),topic);

        //四种情况
        //评论帖子：用户名、帖子内容、评论内容、发送时间
        //回复评论：用户名、评论内容、回复内容、发送时间
        //点赞帖子：用户名、帖子内容、点赞时间
        //点赞评论：用户名、评论内容、点赞时间

        //用于显示的通知列表
        List<Map<String,Object>> adviceVOList = new ArrayList<>();
        for(Message msg:allAdviceByTopic){//遍历每一个通知
            Map<String,Object> adviceVO = new HashMap<>();//封装消息

            //消息主体
            Map msgBody = JSONObject.parseObject(msg.getContent(), Map.class);
            //发送者用户名
            String senderUserName = userService.findById(Integer.parseInt(msgBody.get("fromId").toString())).getUsername();
            //被评论、回复或点赞的对象内容
            String content = null;
            int entityType = Integer.parseInt(msgBody.get("entityType").toString());
            int entityId = Integer.parseInt(msgBody.get("entityId").toString());
            if(entityType==ENTITY_TYPE_DISCUSSPOST)
                content = userService.findDiscussPostById(entityId).getContent();
            else
                content = commentService.getCommentById(entityId).getContent();
            //发送者评论内容
            String comment = msgBody.get("comment").toString();
            //创建时间
            Date createTime = msg.getCreateTime();

            adviceVO.put("senderUserName",senderUserName);
            adviceVO.put("content",content);
            adviceVO.put("comment",comment);
            adviceVO.put("createTime",createTime);

            adviceVOList.add(adviceVO);
        }

        model.addAttribute("adviceVOList",adviceVOList);
        model.addAttribute("topic",topic);

        return "site/notice-detail";
    }

    //获取某一个主题下的最新通知
    @RequestMapping("/getLastAdviceByTopic")
    @ResponseBody
    public String getLastAdviceByTopic(String topic){
        //当前登录用户
        User user = hostHolder.get();
        //获取当前主题下的最新通知
        Message lastMessage = messageService.getLastAdviceByTopic(user.getId(), topic);
        if(lastMessage==null){
            return null;
        }
        //通知主体
        Map msgBody = JSONObject.parseObject(lastMessage.getContent(), Map.class);
        //发送者
        String senderUserName = userService.findById(Integer.parseInt(msgBody.get("fromId").toString())).getUsername();
        //发送者评论内容
        String comment = msgBody.get("comment").toString();

        Map<String,Object> vo = new HashMap<>();
        vo.put("senderUserName",senderUserName);
        vo.put("comment",comment);
        return JSONObject.toJSONString(vo);
    }



}
