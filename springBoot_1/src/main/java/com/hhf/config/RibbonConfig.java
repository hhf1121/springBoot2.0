package com.hhf.config;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import com.hhf.utils.SentinelExceptionUtil;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 2020-2-8 18:02:49
 */
@Configuration
public class RibbonConfig {

    //sentinel组件：handleExceptionLimit限流方法、fallbackFailuer降级方法
    @SentinelRestTemplate(blockHandler = "handleExceptionLimit",blockHandlerClass = SentinelExceptionUtil.class, fallback = "fallbackFailuer",fallbackClass = SentinelExceptionUtil.class)
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate( ) {
        return new RestTemplate();
    }
}
