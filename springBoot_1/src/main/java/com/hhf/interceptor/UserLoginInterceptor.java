package com.hhf.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONArray;
import com.hhf.entity.User;
import com.hhf.rocketMQ.RegisterConsumer;
import com.hhf.utils.CurrentUserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

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
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    CurrentUserContext currentUserContext;

    @Value("${server.port}")
    private String port;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler)throws Exception {
        log.info("当前服务器ip："+port);
        Cookie[] cookies = request.getCookies();
        String token = "";
        if(cookies!=null&&cookies.length>0){
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(),"myToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        String s = stringRedisTemplate.opsForValue().get(token);
        if(StringUtils.isBlank(s)){
            //直接重定向到登录页面
            logger.info("未登录");
//            response.setStatus(401);
            response.sendError(401);
            response.sendRedirect("http://localhost:8081/#/Login");
            return false;
        }
//        redis、转换成user对象
        User user = JSONArray.parseObject(s, User.class);
        currentUserContext.setUser(user);
        //redis续时间
        stringRedisTemplate.opsForValue().set(token,s,30, TimeUnit.MINUTES);
        logger.info("已登录");
        return true;
    }

   public StringRedisTemplate getRedis(){
        return this.stringRedisTemplate;
    }

}