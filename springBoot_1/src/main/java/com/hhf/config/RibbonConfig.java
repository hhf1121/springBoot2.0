package com.hhf.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 2020-2-8 18:02:49
 */
@Configuration
public class RibbonConfig {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate( ) {
        return new RestTemplate();
    }
}
