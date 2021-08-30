package com.hhf.feignClient.configuration;


import feign.Contract;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("loginConfiguration")
public class LoginConfiguration {
//
    @Bean("springMvcContract")
    public Contract feignContract() {
        return new SpringMvcContract();
    }
}
