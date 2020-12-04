package com.hhf.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 定时任务
 */
@Component
@Slf4j
public class MyTask {

    @Autowired
    private StringRedisTemplate template;


    @Scheduled(cron = "0 0 0 * * ?")
    private void removeRedisOfBrithday() {
        Set<String> keys = template.keys("brithday:*");
        Long delete = template.delete(keys);
        log.info("定时清除生日缓存，条数:"+delete+"，当前时间："+ LocalDateTime.now());
    }

}
