package com.hhf.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * description:
 * author:hhf
 * datetime:2020-1-31 19:33:56
 */
@Slf4j
@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    protected org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler)throws Exception {

        Cookie[] cookies = request.getCookies();
        String token = "";
        for (Cookie cookie : cookies) {
            if (StringUtils.equals(cookie.getName(),"myToken")) {
                token = cookie.getValue();
            }
        }
        String s = stringRedisTemplate.opsForValue().get(token);
        if(StringUtils.isBlank(s)){
            //直接重定向到登录页面
            logger.info("未登录");
            response.sendRedirect("http://localhost:8081/#/Login");
            return false;
        }
        logger.info("已登录");
        return true;
    }

}