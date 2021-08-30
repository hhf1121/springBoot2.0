package com.hhf.enums;

import lombok.Data;

/**
 * 云路供应链科技有限公司 版权所有 © Copyright 2020
 *
 * @author hehongfei
 * @Description:
 * @date 2021-08-30
 */

public enum RedisKeyEnum {


    USER("用户","BASE:USER:INFO:"),
    WS_ONLINE("用户在线","BASE:WS:ONLINE:"),
    ADDRESS("地址","BASE:ADDRESS:INFO");


    private String name;

    private String code;


    RedisKeyEnum(String name,String code){
     this.name=name;
     this.code=code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
