package com.hhf.feignClient;

import com.alibaba.fastjson.JSONObject;
import com.hhf.feignClient.fallback.LoginFeignFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import com.hhf.feignClient.configuration.LoginConfiguration;

@FeignClient(
        value = "login",
        url = "http://localhost:8080",
        path = "/wx",
        configuration = LoginConfiguration.class,
        fallback = LoginFeignFallBack.class
)
public interface LoginFeign{

    @RequestMapping("/getCode")
    String getCode();

    @RequestMapping("/getTicket")
    String getTicket();


    @RequestMapping("/loginCallback")
    public JSONObject loginCallback(@RequestParam("code")String code, @RequestParam("state")String state);


    @RequestMapping("/getAppinfo")
    public Map<String,Object> getAppinfo();


}
