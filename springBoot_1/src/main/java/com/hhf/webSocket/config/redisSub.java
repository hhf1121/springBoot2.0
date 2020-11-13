package com.hhf.webSocket.config;


import com.alibaba.fastjson.JSONObject;
import com.hhf.webSocket.WebSocketServer;
import com.hhf.webSocket.config.dto.WebSocketDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author litong
 */
@Service(value = "redisMessageListener")
@Slf4j
public class redisSub implements MessageListener {

    @Autowired
    private WebSocketServer webSocketServer;

    @Value("${server.port}")
    private String port;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("redis订阅消费,服务端口:"+port);
        WebSocketDto entity = JSONObject.parseObject(message.getBody(), WebSocketDto.class);
        String channel = (String) redisTemplate.getValueSerializer().deserialize(message.getChannel());
        List<String> agentCodes = entity.getAgentCodes();
        if (!StringUtils.isEmpty(channel) && !agentCodes.isEmpty()) {
            for (String agentCode : agentCodes) {
                // 向客户端推送消息
                webSocketServer.sendOneMessage(agentCode,entity.getType());
            }
        }

    }
}
