package com.work.qq_system_springboot.controller;


import com.work.qq_system_springboot.entity.Group;
import com.work.qq_system_springboot.entity.Message;
import com.work.qq_system_springboot.entity.OprLog;
import com.work.qq_system_springboot.entity.User;
import com.work.qq_system_springboot.service.*;
import com.work.qq_system_springboot.tools.HostHolder;
import com.work.qq_system_springboot.tools.QQSystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.work.qq_system_springboot.tools.QQSystemConstant.LOG_TYPE_INTERACTIVE;

@Controller
@RequestMapping("/relation")
public class RelationController {

    @Autowired
    private RelationService relationService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private OprLogService oprLogService;

    @Value("${community.file_system.path}")
    private String fileRoot;

    //请求添加好友
    @RequestMapping("/requestAddFriend/{toId}/{valMsg}")
    @ResponseBody
    public void requestAddFriend(@PathVariable("toId") int toId,
                                 @PathVariable("valMsg") String valMsg,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {
        //从本地线程中获取当前登录用户
        User user = hostHolder.get();
        Map<String, Object> map = relationService.addFriend(user.getId(), toId, valMsg);
        //response向客户端返回状态消息
        response.setCharacterEncoding("utf8");
        if(map==null||map.isEmpty()){
            response.getWriter().write("成功发送好友申请！");
        }else{
            response.getWriter().write(map.get("msg").toString());
        }





        //记录日志
        String targetName = userService.findById(toId).getUsername();
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("请求添加\""+targetName+"\"为好友");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

    }

    //群通知页面
    @RequestMapping("/getVarifyGroupPage")
    public String getVarifyGroupPage(HttpServletRequest request,Model model){

        User user = hostHolder.get();
        //若干个群，每个群包括若干个验证用户
        List<Map<String,Object>> voValGroup = new ArrayList<>();
        Map<String,Object> map = null;
        //获取当前用户管理的群(level>0，不能为普通用户)
        List<Integer> allEnteredGroup = relationService.getAllEnteredGroup(user.getId(), 1);
        Iterator<Integer> iterator = allEnteredGroup.iterator();
        int joinGroupApplyCount = 0;
        while (iterator.hasNext()){//遍历每一个群
            int groupId = iterator.next();
            List<User> allVarifyUser = relationService.getAllJoinGroupApply(groupId);//获取当前群的所有待验证用户
            Group group = groupService.findById(groupId);
            System.out.println("群："+group.getGroupName()+"的验证用户个数为："+allVarifyUser.size());
            if(allVarifyUser.size()>0){
                map = new HashMap<>();
                map.put("group",group);
                map.put("allVarifyUser",allVarifyUser);
                voValGroup.add(map);
                joinGroupApplyCount += allVarifyUser.size();
            }
        }

        model.addAttribute("voValGroup",voValGroup);
        model.addAttribute("joinGroupApplyCount",joinGroupApplyCount);
        List<User> allVarifyFriend = relationService.getAllVarifyFriend(hostHolder.get().getId());
        model.addAttribute("allVarifyFriendCount",allVarifyFriend.size());

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("进入群通知页面");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "site/varify_group";
    }

    //获取所有进群验证
    @RequestMapping("/getJoinGroupApplyCount")
    @ResponseBody
    public int getJoinGroupApplyCount(){
        int cnt = 0;
        List<Integer> allEnteredGroup = relationService.getAllEnteredGroup(hostHolder.get().getId(), 1);
        for (Integer integer : allEnteredGroup) {
            List<User> allJoinGroupApply = relationService.getAllJoinGroupApply(integer);
            cnt += allJoinGroupApply.size();
        }
        return cnt;
    }


    //请求好友验证页面
    @RequestMapping("/getVarifyFriendPage")
    public String getVarifyFriendPage(HttpServletRequest request,Model model) {

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("进入好友验证页面");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        List<User> allVarifyFriend = relationService.getAllVarifyFriend(hostHolder.get().getId());
        model.addAttribute("allVarifyFriend",allVarifyFriend);
        model.addAttribute("allVarifyFriendCount",allVarifyFriend.size());//好友验证个数
        int cnt = 0;//进群验证个数
        List<Integer> allEnteredGroup = relationService.getAllEnteredGroup(hostHolder.get().getId(), 1);
        for (Integer integer : allEnteredGroup) {
            List<User> allJoinGroupApply = relationService.getAllJoinGroupApply(integer);
            cnt += allJoinGroupApply.size();
        }
        model.addAttribute("joinGroupApplyCount",cnt);//好友验证个数

        return "site/varify_friend";
    }




    //获取所有验证好友
    @RequestMapping("/getAllVarifyFriend")
    @ResponseBody
    public String getAllVarifyFriend(){

        User user = hostHolder.get();

        List<User> allVarifyFriend = relationService.getAllVarifyFriend(user.getId());
        String res = "";
        for (User user1 : allVarifyFriend) {
            res += user1.getId()+","+user1.getUsername()+","+user1.getHeaderUrl()+","+user1.getValMsg()+";";
        }

        if(res.length()>0)
            res = res.substring(0,res.length()-1);

        return res;

    }


    //接收好友申请
    @RequestMapping("/acceptFriend/{friendId}")
    @ResponseBody
    public void acceptFriend(@PathVariable("friendId") int friendId,HttpServletRequest request) throws IOException {

        //获取当前用户
        User user = hostHolder.get();
        //接受好友申请
        relationService.acceptFriend(friendId,user.getId());

        //记录日志
        String targetName = userService.findById(friendId).getUsername();
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("接受了好友\""+targetName+"\"的申请");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);
    }

    //拒绝好友申请
    @RequestMapping("/rejectFriend/{friendId}")
    @ResponseBody
    public void rejectFriend(@PathVariable("friendId") int friendId,HttpServletRequest request) throws IOException {

        //获取当前用户
        User user = hostHolder.get();
        //拒绝好友申请
        relationService.rejectFriend(friendId,user.getId());



        //记录日志
        String targetName = userService.findById(friendId).getUsername();
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("拒绝了好友\""+targetName+"\"的申请");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);
    }

    //进群申请管理
    @RequestMapping("/applyJoinGroupManage/{userId}/{groupId}/{isReject}")
    public void applyJoinGroupManage(@PathVariable("userId")int userId
            ,@PathVariable("groupId")int groupId
            ,@PathVariable("isReject")int isReject
            ,HttpServletRequest request){

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setType(LOG_TYPE_INTERACTIVE);

        User user = userService.findById(userId);
        Group group = groupService.findById(groupId);

        if(isReject==1){
            relationService.rejectJoinGroupApply(groupId,userId);
            oprLog.setInfo("拒绝用户："+user.getUsername()+"进入群聊："+group.getGroupName());
        }else{
            relationService.acceptJoinGroupApply(groupId,userId);
            oprLog.setInfo("接受用户："+user.getUsername()+"进入群聊："+group.getGroupName());
        }
        oprLogService.addOprLog(oprLog);
    }

    //获取好友列表页面
    @RequestMapping("/getFriendListPage")
    public String getFeiendListPage(String isGroup, HttpServletRequest request,Model model) throws IOException {

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setType(LOG_TYPE_INTERACTIVE);


        List<Map<String,Object>> list = new ArrayList<>();
        //获取当前登录用户
        User user = hostHolder.get();

        if(isGroup.equals("0")){//获取好友列表
            oprLog.setInfo("进入好友列表页面");
            //当前用户的所有好友
            List<User> allFriend = relationService.getAllFriend(user.getId());
            for (User friend : allFriend) {
                //获取当前用户发送的未读消息
                int unreadCount = messageService.getUnreadCountBySomeBody(friend.getId(),user.getId());
                Message latestMessages = messageService.getLatestMessages(friend.getId(), user.getId());

                Map<String,Object> map = new HashMap<>();
                map.put("unreadCount",unreadCount);
                map.put("friend",friend);
                map.put("latestMessages",latestMessages);
                list.add(map);
            }
        }else{//获取群聊列表
            oprLog.setInfo("进入群聊列表页面");
            List<Integer> allEnteredGroupId = relationService.getAllEnteredGroup(user.getId(),0);
            Iterator<Integer> iterator = allEnteredGroupId.iterator();
            while (iterator.hasNext()){
                Group group = groupService.findById(iterator.next());
                System.out.println("------>"+group);
                Message latestMessages = messageService.getLatestMessagesByGroupId(group.getId());
                System.out.println(latestMessages);
                Map<String,Object> map = new HashMap<>();
                map.put("group",group);
                map.put("latestMessages",latestMessages);
                list.add(map);
            }
        }
        model.addAttribute("list",list);
        model.addAttribute("isGroup",isGroup);//有可能是群组列表，而不是朋友列表

        //操作日志
        oprLogService.addOprLog(oprLog);
        return "site/friend_list";
    }


    //添加群聊
    @RequestMapping("/addGroup")
    @ResponseBody
    public String addGroup(String groupName,HttpServletRequest request){
        groupService.addGroup(groupName, hostHolder.get().getId());

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("创建了群："+groupName);
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "创建成功！";
    }





}
