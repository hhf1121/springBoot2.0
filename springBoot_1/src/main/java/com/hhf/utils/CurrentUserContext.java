package com.hhf.utils;

import com.alibaba.fastjson.JSONObject;
import com.hhf.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Component
public class CurrentUserContext {

    public final static String OAUTH_APP_TOKEN="OAUTH_APP_TOKEN";
    public final static String USER_INFO="USER_INFO";
    public final static String USER_ID_KEY="USER_ID_KEY";

    private static User user;

    public static User getCurrentUser() {
        String userJson = RequestContextHolder.getRequestAttributes().getAttribute(CurrentUserContext.USER_INFO, 0).toString();
        User loginVO = JSONObject.parseObject(userJson, User.class);
        if (loginVO == null) {
            log.error("用户获取失败");
            return null;
        }
        return loginVO;
    }

    public static void setUser(User user) {
        CurrentUserContext.user = user;
    }

    public static User getUser() {
        return user;
    }

}
