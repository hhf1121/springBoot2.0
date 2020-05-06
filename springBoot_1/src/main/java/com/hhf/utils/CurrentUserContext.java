package com.hhf.utils;

import com.hhf.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserContext {

    private static User user;

    public static User getCurrentUser() {
        return user;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        CurrentUserContext.user = user;
    }

}
