package com.hhf.config;


import feign.Contract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支持http调用
 */
@Configuration
public class FeignConfiguration {
    //使用feign自带契约
    @Bean
    public Contract feignContract() {
        return new Contract.Default();
    }
}