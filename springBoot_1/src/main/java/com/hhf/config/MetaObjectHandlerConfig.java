package com.hhf.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.hhf.utils.CurrentUserContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *  mybatisPlus自动填充
 */

@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

//    @Autowired
//    CurrentUserContext currentUserContext;

    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        setFieldValByName("createrTime", now, metaObject);
        setFieldValByName("creater", CurrentUserContext.getCurrentUser().getName(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Date now = new Date();
        this.setFieldValByName("modifierTime", now, metaObject);
        this.setFieldValByName("modifier", CurrentUserContext.getCurrentUser().getName(), metaObject);
    }
}