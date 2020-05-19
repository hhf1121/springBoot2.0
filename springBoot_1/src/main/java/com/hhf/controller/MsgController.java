package com.hhf.controller;


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


}
