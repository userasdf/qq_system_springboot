package com.work.qq_system_springboot.controller;


import com.work.qq_system_springboot.entity.Group;
import com.work.qq_system_springboot.entity.OprLog;
import com.work.qq_system_springboot.entity.User;
import com.work.qq_system_springboot.service.GroupService;
import com.work.qq_system_springboot.service.OprLogService;
import com.work.qq_system_springboot.service.RelationService;
import com.work.qq_system_springboot.service.UserService;
import com.work.qq_system_springboot.tools.HostHolder;
import com.work.qq_system_springboot.tools.QQSystemConstant;
import com.work.qq_system_springboot.tools.QQSystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.work.qq_system_springboot.tools.QQSystemConstant.LOG_TYPE_GAME;

@Controller
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private RelationService relationService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private GroupService groupService;

    @Autowired
    private OprLogService oprLogService;

    @Autowired
    private UserService userService;

    //进入群详细信息页面
    @RequestMapping("/getGroupUserPage")
    public String getGroupUserPage(int groupId, Model model, HttpServletRequest request){

        //设置当前用户的群成员等级
        int level = groupService.getGroupUserLevel(groupId,hostHolder.get().getId());
        hostHolder.get().setGroupLevel(level);

        //获取群详细信息
        Group group = groupService.findById(groupId);
        List<Integer> members = relationService.getAllUserByGroupId(groupId, 0);
        List<User> userList = new ArrayList<>();
        for (Integer member : members) {
            User u = userService.findById(member);
            u.setGroupLevel(groupService.getGroupUserLevel(groupId,u.getId()));
            userList.add(u);
        }
        //创建者信息
        User createUser = userService.findById(group.getCreateUser());
        model.addAttribute("group",group);
        model.addAttribute("createUser",createUser);
        model.addAttribute("userList",userList);
        model.addAttribute("user",hostHolder.get());


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("进入群："+group.getGroupName()+"的详细信息页面");
        oprLog.setType(QQSystemConstant.LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "site/group_user";
    }

    //更新群成员级别
    @RequestMapping("/updateUserLevel")
    @ResponseBody
    public void updateUserLevel(int groupId,int userId,int level,HttpServletRequest request){
        groupService.updateGroupUserLevel(level,groupId,userId);

        Group byId = groupService.findById(groupId);
        User user = userService.findById(userId);

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("更新："+byId.getGroupName()+"的成员："+user.getUsername()+"群等级为："+level);
        oprLog.setType(QQSystemConstant.LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);
    }

}
