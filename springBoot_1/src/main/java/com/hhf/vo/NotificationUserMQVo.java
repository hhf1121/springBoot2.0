package com.hhf.vo;

import lombok.Data;

import java.util.List;

/**
 * 通知用户mq
 */

@Data
public class NotificationUserMQVo {

    //用户id
    private List<String> userIds;

    //通知类型
    private String type;

    //通知的消息
    private String msg;


}
