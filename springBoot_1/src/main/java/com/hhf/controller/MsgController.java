package com.hhf.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhf.entity.BaseMsg;
import com.hhf.service.IMsgService;
import com.hhf.utils.ResultUtils;
import com.hhf.vo.MsgVo;
import com.hhf.webSocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("msg")
public class MsgController {

    @Autowired
    private IMsgService msgService;

    @Autowired
    private WebSocketServer webSocketServer;

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
    @PostMapping("/sendAllWebSocket")
    public Map<String,Object> sendAllWebSocket(String msg) {
        webSocketServer.sendAllMessage(msg);
        return ResultUtils.getSuccessResult("发送成功");
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
