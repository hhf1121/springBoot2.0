package com.hhf.controller;


import afu.org.checkerframework.checker.oigj.qual.O;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhf.entity.BaseMsg;
import com.hhf.service.IMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("msg")
public class MsgController {

    @Autowired
    private IMsgService msgService;

    @PostMapping("/getMsg")
    public IPage<BaseMsg> getMsg(@RequestBody BaseMsg baseMsg){
        return  msgService.getMsg(baseMsg);
    }

    @GetMapping("/getMsgCount")
    public Map<String,Object> getMsgCount(){
        return  msgService.getMsgCount();
    }

    /**
     * 标记已读
     * @param baseMsg
     * @return
     */
    @PostMapping("/signRead")
    public Map<String,Object> signRead(@RequestBody BaseMsg baseMsg){
        return  msgService.signRead(baseMsg);
    }


    /**
     * 批量删除
     * @param baseMsg
     * @return
     */
    @PostMapping("/deleteMsgById")
    public Map<String,Object> deleteMsgById(@RequestBody BaseMsg baseMsg){
        return  msgService.deleteMsgById(baseMsg);
    }

}
