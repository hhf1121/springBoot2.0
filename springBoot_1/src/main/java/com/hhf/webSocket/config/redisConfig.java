package com.hhf.webSocket.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @author litong
 * @date 2019/10/17 14:33
 */
@Configuration
public class redisConfig {
    @Autowired
    private StringRedisTemplate redisTemplate;


    @Bean(destroyMethod = "destroy")
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory,
                                                                       MessageListener redisMessageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);

        //可以添加多个 messageListener
        container.addMessageListener(redisMessageListener, new PatternTopic("ws:message"));

        return container;
    }


}
