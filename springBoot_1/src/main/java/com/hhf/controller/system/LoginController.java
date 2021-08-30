package com.hhf.controller.system;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hhf.feignClient.LoginFeign;
import com.hhf.service.UserService;
import com.hhf.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/login/code")
public class LoginController {

    @Autowired
    private LoginFeign loginFeign;

    @Autowired
    private UserService userService;

    @RequestMapping("/getAppInfo")
    public Map<String,Object> getAppInfo(){
        return ResultUtils.getSuccessResult(loginFeign.getAppinfo());
    }


    @RequestMapping("/getCode")
    public Map<String,Object> getCode(){
        return ResultUtils.getSuccessResult(loginFeign.getCode());
    }


    @RequestMapping("/getTicket")
    public Map<String,Object> getTicket(){
        return ResultUtils.getSuccessResult(loginFeign.getTicket());
    }


    @RequestMapping("/loginCallback")
    public Map<String,Object> loginCallback(@RequestParam("code")String code, @RequestParam("state")String state){
        JSONObject jsonObject = loginFeign.loginCallback(code, state);
        log.info("用户信息："+JSON.toJSONString(jsonObject));
//        userService
        return ResultUtils.getSuccessResult(jsonObject);
    }




}
