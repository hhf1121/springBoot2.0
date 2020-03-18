package com.hhf.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * description:
 * author:hhf
 * datetime:2020-1-31 19:33:56
 */
@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {

    protected org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler)throws Exception {

        HttpSession session = request.getSession(true);
        Object username=session.getAttribute("currentUser");
//        username="";
        if(null!=username) {//已登录
            logger.info("已登录");
            return true;
        }else {//未登录
            //直接重定向到登录页面
            logger.info("未登录");
            response.sendRedirect("local/");
            return false;
        }
    }

}