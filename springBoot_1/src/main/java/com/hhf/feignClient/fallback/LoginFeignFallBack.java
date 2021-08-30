package com.hhf.feignClient.fallback;

import com.alibaba.fastjson.JSONObject;
import com.hhf.feignClient.LoginFeign;
import com.hhf.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class LoginFeignFallBack implements LoginFeign {
    @Override
    public String getCode() {
        log.error("getCode调用失败");
        return "";
    }

    @Override
    public String getTicket() {
        return null;
    }

    @Override
    public JSONObject loginCallback(String code, String state) {
         log.error("loginCallback调用失败");
        return null;
    }

    @Override
    public Map<String, Object> getAppinfo() {
        log.error("getAppinfo调用失败");
        return ResultUtils.getFailResult("调用失败");
    }
}
