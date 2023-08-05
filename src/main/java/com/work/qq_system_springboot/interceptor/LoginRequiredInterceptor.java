package com.work.qq_system_springboot.interceptor;

import com.work.qq_system_springboot.entity.User;
import com.work.qq_system_springboot.tools.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor{


    @Autowired
    private HostHolder hostHolder;

    //登录前检查
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        StringBuffer requestURL = request.getRequestURL();

        User user = hostHolder.get();
        if(user==null){
            response.sendRedirect("/qq_system/getLoginPage");
            System.out.println("拦截到请求："+requestURL);
            return false;
        }
        return true;
    }
}