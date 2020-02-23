package com.hhf.utils;
//
///**
// * 全局异常处理类
// */

import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by smlz on 2019/11/27.
 */
@Slf4j
public class SentinelExceptionUtil {


    /**
     * 限流后处理方法
     * @param request
     * @param body
     * @param execution
     * @param ex
     * @return
     */
    public static SentinelClientHttpResponse handleExceptionLimit(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException ex)  {

        Map<String, Object> productInfo = new HashMap<String, Object>();
        productInfo.put("success",false);
        productInfo.put("data","被限流了");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return new SentinelClientHttpResponse(objectMapper.writeValueAsString(productInfo));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 熔断后处理的方法
     * @param request
     * @param body
     * @param execution
     * @param ex
     * @return
     */
    public static SentinelClientHttpResponse fallbackFailuer(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException ex) {
        Map<String, Object> productInfo = new HashMap<String, Object>();
        productInfo.put("success",false);
        productInfo.put("data","被降级了");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return new SentinelClientHttpResponse(objectMapper.writeValueAsString(productInfo));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
