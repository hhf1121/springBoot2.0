package com.hhf.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhf.entity.BaseMsg;
import com.hhf.service.IMsgService;
import com.hhf.utils.ResultUtils;
import com.hhf.vo.MsgVo;
import com.hhf.webSocket.MsgWebSocketServer;
import com.hhf.webSocket.config.dto.WebSocketDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("msg")
public class MsgController {

    @Autowired
    private IMsgService msgService;

    @Autowired
    private MsgWebSocketServer webSocketServer;

    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    //生产者的组名
    DefaultMQProducer producer= null;

    @PostConstruct
    public void getMQ(){
        producer=new DefaultMQProducer("msgProducer");
        //指定NameServer地址，多个地址以 ; 隔开
        producer.setNamesrvAddr(namesrvAddr);
        try {
            producer.start();
            log.info("mq启动成功...");
        } catch (MQClientException e) {
            log.info(e.getMessage());
            log.info("mq启动失败...");
        }
    }


    @PostMapping("/getMsg")
    public IPage<BaseMsg> getMsg(@RequestBody BaseMsg baseMsg){
        return  msgService.getMsg(baseMsg);
    }

    /**
     * 前端轮询是否有新消息
     * @return
     */
    @GetMapping("/getMsgCount")
    public Map<String,Object> getMsgCount(){
        return  msgService.getMsgCount();
    }

    /**
     * 标记已读
     * @param baseMsgList
     * @return
     */
    @PostMapping("/signRead")
    public Map<String,Object> signRead(@RequestBody MsgVo baseMsgList){
        return  msgService.signRead(baseMsgList);
    }


    /**
     * 批量删除
     * @param baseMsgList
     * @return
     */
    @PostMapping("/deleteMsgs")
    public Map<String,Object> deleteMsgs(@RequestBody MsgVo baseMsgList){
        return  msgService.deleteMsgs(baseMsgList);
    }

    //用户通过MQ，互相发信息
    @PostMapping("/sendMsg")
    public Map<String, Object> sendMsg(@RequestBody BaseMsg baseMsg, HttpServletRequest request) {
        return msgService.sendMsg(baseMsg,request);
    }

    /**
     * 在线即时消息，不做持久保存
     * @param msg
     * @return
     */
    @GetMapping("/sendAllWebSocket")
    public Map<String,Object> sendAllWebSocket(String msg) {
        return msgService.sendAllMsg(msg);
    }


    /**
     * 在线即时消息，不做持久保存(弹幕)
     * @param msg
     * @return
     */
    @PostMapping("/sendDMAllWebSocket")
    public Map<String,Object> sendDMAllWebSocket(@RequestBody  WebSocketDto msg) {
        return msgService.sendAllMsgByDM(msg);
    }

    @PostMapping("/sendOneWebSocket")
    public Map<String,Object> sendOneWebSocket(@RequestBody BaseMsg baseMsg) {
        String text=baseMsg.getToId()+":"+baseMsg.getMsg();
        //消息存入redis
        int size= msgService.saveOrUpdateMsgRedis(baseMsg);
        //webSocket通知：
        webSocketServer.sendOneMessage(baseMsg.getToId()+"",size+"");
        return ResultUtils.getSuccessResult("发送成功");
    }



}
