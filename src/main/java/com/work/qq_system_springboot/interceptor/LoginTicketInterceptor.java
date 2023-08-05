package com.work.qq_system_springboot.interceptor;

import com.work.qq_system_springboot.entity.LoginTicket;
import com.work.qq_system_springboot.entity.User;
import com.work.qq_system_springboot.service.UserService;
import com.work.qq_system_springboot.tools.CookieUtil;
import com.work.qq_system_springboot.tools.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor{

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //处理前检查
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ticket = CookieUtil.getValue(request, "ticket");
        if(ticket!=null){
            //获取用户登录凭证
            LoginTicket loginTicket = userService.findLoginTiekctByTicket(ticket);
            if(loginTicket!=null&&loginTicket.getStatus()==0&&loginTicket.getExpired().after(new Date())){
                User user = userService.findById(loginTicket.getUserId());
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    //处理后返回前保存数据
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        User user = hostHolder.get();
        if(user!=null&&modelAndView!=null){
            modelAndView.addObject("user",user);
        }
    }

    //处理后清除数据
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        hostHolder.clear();
    }
}
