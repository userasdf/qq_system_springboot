package com.work.qq_system_springboot.controller;

import com.google.code.kaptcha.Producer;
import com.work.qq_system_springboot.entity.OprLog;
import com.work.qq_system_springboot.entity.User;
import com.work.qq_system_springboot.mapper.LoginTicketMapper;
import com.work.qq_system_springboot.mapper.UserMapper;
import com.work.qq_system_springboot.service.OprLogService;
import com.work.qq_system_springboot.service.UserService;
import com.work.qq_system_springboot.tools.FileOperation;
import com.work.qq_system_springboot.tools.HostHolder;
import com.work.qq_system_springboot.tools.QQSystemConstant;
import com.work.qq_system_springboot.tools.QQSystemUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@Controller
public class LoginController implements QQSystemConstant{

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private UserMapper userMapper;

    //用于生成验证码
    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private OprLogService oprLogService;

    //获取文件根路径
    @Value("${community.file_system.path}")
    private String fileRoot;




    //获取登录页面
    @RequestMapping("/getLoginPage")
    public String getLoginPage(HttpServletRequest request) throws IOException {


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(UNSIGNED_USER_ID);
        oprLog.setUserName("未登录用户");
        oprLog.setInfo("进入了登录页面");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        User user = hostHolder.get();
        if(user!=null){
            oprLog.setUserId(user.getId());
            oprLog.setUserName(user.getUsername());
        }
        oprLogService.addOprLog(oprLog);

        return "site/login";
    }


    @RequestMapping("/")
    public String getDefaultPage(HttpServletRequest request) throws IOException {


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(UNSIGNED_USER_ID);
        oprLog.setUserName("未登录用户");
        oprLog.setInfo("直接访问qq_system，没有输入index");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        User user = hostHolder.get();
        if(user!=null){
            oprLog.setUserId(user.getId());
            oprLog.setUserName(user.getUsername());
        }
        oprLogService.addOprLog(oprLog);

        return "redirect:index";
    }

    @RequestMapping("/userMain")
    public String userMain(HttpServletRequest request) throws IOException {


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(UNSIGNED_USER_ID);
        oprLog.setUserName("未登录用户");
        oprLog.setInfo("【userMain】访问入口");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        User user = hostHolder.get();
        if(user!=null){
            oprLog.setUserId(user.getId());
            oprLog.setUserName(user.getUsername());
        }
        oprLogService.addOprLog(oprLog);

        return "redirect:index";
    }


    //获取主页面（云文件系统页面）
    @RequestMapping("/index")
    public String index(String tempDir,String isAbsolutePath,HttpServletRequest request, Model model) throws IOException {



        //获取当前用户
        User user = hostHolder.get();
        //当前用户根目录
        String userRoot = "/id_"+user.getId();

        if(tempDir==null) {
            tempDir = userRoot;
        }
        if(isAbsolutePath==null){
            isAbsolutePath = "0";
        }
        model.addAttribute("tempDir",tempDir);
        model.addAttribute("isAbsolutePath",isAbsolutePath);
        model.addAttribute("user",user);

        //获取用户根目录
        String root = fileRoot+"/"+userRoot;
        System.out.println("root:"+root);
        //如果用户根目录不存在，则创建根目录
        File file = new File(root);
        if(!file.exists()){
            file.mkdirs();
            FileOperation.copyOrMoveDir(new File(fileRoot+"/public_data"),new File(root),false);
        }



        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("进入了主页面");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);


        return "index";
    }

    //获取注册页面
    @RequestMapping("/getRegisterPage")
    public String getRegisterPage(HttpServletRequest request) throws IOException {



        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(UNSIGNED_USER_ID);
        oprLog.setUserName("未登录用户");
        oprLog.setInfo("进入了注册页面");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        User user = hostHolder.get();
        if(user!=null){
            oprLog.setUserId(user.getId());
            oprLog.setUserName(user.getUsername());
        }
        oprLogService.addOprLog(oprLog);


        return "site/register";
    }

    //注册
    @RequestMapping(value = "register",method = RequestMethod.POST)
    public String register(User user, Model model,HttpServletRequest request){
        Map<String, Object> map = userService.register(user);
        if(map==null||map.isEmpty()){//注册成功！返回操作结果页面
            model.addAttribute("msg","注册成功，我们已经向您发送了一封邮箱，请尽快激活！");
            model.addAttribute("target","getLoginPage");


            //记录日志
            OprLog oprLog = QQSystemUtil.getRequestInfo(request);
            oprLog.setUserId(UNSIGNED_USER_ID);
            oprLog.setUserName("未登录用户");
            oprLog.setInfo("注册成功！");
            oprLog.setType(LOG_TYPE_INTERACTIVE);
            oprLogService.addOprLog(oprLog);


            return "site/operate-result";//返回操作结果页面
        }else{//注册失败！
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));


            //记录日志
            OprLog oprLog = QQSystemUtil.getRequestInfo(request);
            oprLog.setUserId(UNSIGNED_USER_ID);
            oprLog.setUserName("未登录用户");
            String errMsg = map.get("usernameMsg")!=null?map.get("usernameMsg").toString():map.get("emailMsg").toString();
            oprLog.setInfo("注册失败："+errMsg);
            oprLog.setType(LOG_TYPE_INTERACTIVE);
            oprLogService.addOprLog(oprLog);


            return "site/register";//返回注册页面
        }


    }

    //用户激活
    @RequestMapping("/activation/{id}/{code}")
    public String activation(@PathVariable("id")int id,@PathVariable("code")String code,Model model,HttpServletRequest request){
        int res = userService.activation(id, code);
        System.out.println("res:"+res);
        if(res==ACTIVATION_REPEAT){
            model.addAttribute("msg","该用户已经激活，请勿重复激活！");
            model.addAttribute("target","/getLoginPage");


            //记录日志
            OprLog oprLog = QQSystemUtil.getRequestInfo(request);
            oprLog.setUserId(UNSIGNED_USER_ID);
            oprLog.setUserName("未登录用户");
            oprLog.setInfo("该用户已激活，请勿重新激活！");
            oprLog.setType(LOG_TYPE_INTERACTIVE);
            oprLogService.addOprLog(oprLog);

        }else if(res==ACTIVATION_SUCCESS){
            model.addAttribute("msg","用户激活成功！");
            model.addAttribute("target","/getLoginPage");

            //记录日志
            OprLog oprLog = QQSystemUtil.getRequestInfo(request);
            oprLog.setUserId(UNSIGNED_USER_ID);
            oprLog.setUserName("未登录用户");
            oprLog.setInfo("用户激活成功！");
            oprLog.setType(LOG_TYPE_INTERACTIVE);
            oprLogService.addOprLog(oprLog);
        }else{
            model.addAttribute("msg","激活码不正确！");
            model.addAttribute("target","/getRegisterPage");

            //记录日志
            OprLog oprLog = QQSystemUtil.getRequestInfo(request);
            oprLog.setUserId(UNSIGNED_USER_ID);
            oprLog.setUserName("未登录用户");
            oprLog.setInfo("激活码不正确！");
            oprLog.setType(LOG_TYPE_INTERACTIVE);
            oprLogService.addOprLog(oprLog);
        }
        return "site/operate-result";
    }

    //生成验证码
    @RequestMapping("/kaptcha")
    public void kaptcha(HttpServletResponse response, HttpSession session) throws IOException {

        //首先生成验证码
        String varifyCode = kaptchaProducer.createText();
        BufferedImage varifyImage = kaptchaProducer.createImage(varifyCode);

        //其次将验证码存入Session
        session.setAttribute("varifyCode",varifyCode);

        //最后将验证码图片发送到客户端
        //设置响应类型
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        ImageIO.write(varifyImage,"png",os);
    }

    //用户登录
    @RequestMapping("/login")
    public String login(String password,String username,String varifyCodeCommited,
                        boolean rememberMe, HttpSession session,HttpServletResponse response,
                        HttpServletRequest request,Model model) throws IOException {

        //首先检查验证码是否正确
        String varifyCodeSession = (String)session.getAttribute("varifyCode");
        if(StringUtils.isBlank(varifyCodeCommited)
                ||StringUtils.isBlank(varifyCodeSession)
                ||!varifyCodeCommited.equalsIgnoreCase(varifyCodeSession)){
            System.out.println(varifyCodeCommited+","+varifyCodeSession);
            model.addAttribute("codeMsg","验证码不正确！");


            //记录日志
            OprLog oprLog = QQSystemUtil.getRequestInfo(request);
            oprLog.setUserId(UNSIGNED_USER_ID);
            oprLog.setUserName(username);
            oprLog.setInfo("登录时输入的验证码不正确！");
            oprLog.setType(LOG_TYPE_INTERACTIVE);
            oprLogService.addOprLog(oprLog);

            return "site/login";
        }

        int expiredSeconds = rememberMe?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){//登录成功，向客户端发送Cookie
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            //记录日志
            int id = userService.findByUsername(username).getId();
            OprLog oprLog = QQSystemUtil.getRequestInfo(request);
            oprLog.setUserId(id);
            oprLog.setUserName(username);
            oprLog.setInfo("登录成功！");
            oprLog.setType(LOG_TYPE_INTERACTIVE);
            oprLogService.addOprLog(oprLog);

            return "redirect:/index";
        }else{//登录失败，返回客户端失败信息
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            String errMsg = map.get("usernameMsg")!=null?map.get("usernameMsg").toString():map.get("passwordMsg").toString();


            OprLog oprLog = QQSystemUtil.getRequestInfo(request);
            oprLog.setUserId(UNSIGNED_USER_ID);
            oprLog.setUserName(username);
            oprLog.setInfo("登录失败："+errMsg);
            oprLog.setType(LOG_TYPE_INTERACTIVE);
            oprLogService.addOprLog(oprLog);

            return "site/login";
        }
    }

    //游客进入
    @RequestMapping("/youke")
    public String youke(HttpServletRequest request,HttpServletResponse response) throws IOException {

        User user = new User();
        String pwd = QQSystemUtil.generateUUID().substring(0,15);
        //执行注册操作
        user.setSalt(QQSystemUtil.generateUUID().substring(0,5));
        user.setUsername("youke"+QQSystemUtil.generateUUID().substring(0,5));
        user.setPassword(QQSystemUtil.md5(pwd+user.getSalt()));
        user.setType(0);//是否是管理员
        user.setStatus(1);//该账号是否激活
        user.setActivationCode(QQSystemUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        user.setPersonalLabel("我是一名游客！");
        userMapper.insert(user);


        int expiredSeconds = REMEMBER_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(user.getUsername(),pwd, expiredSeconds);
        Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
        cookie.setMaxAge(expiredSeconds);
        response.addCookie(cookie);

        //记录日志
        int id = userService.findByUsername(user.getUsername()).getId();
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(id);
        oprLog.setUserName(user.getUsername());
        oprLog.setInfo("游客方式登录成功！");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);



        return "redirect:/index";

    }

    //用户登出
    @RequestMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket,HttpServletRequest request) throws IOException {
        userService.logout(ticket);

        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("用户退出登录！");
        oprLog.setType(LOG_TYPE_INTERACTIVE);
        oprLogService.addOprLog(oprLog);

        return "redirect:/getLoginPage";
    }

}