package com.hhf.webSocket;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@ServerEndpoint("/msgWebSocket/{userId}")
//此注解相当于设置访问URL
public class WebSocketServer {

    /**
     * 无法注入,在启动类里手动注入
     */
    private static StringRedisTemplate stringRedisTemplate;

    /**
     * 当前在线用户 employee,expireTime
     */
    private static ConcurrentHashMap<String, Long> onLineUser = new ConcurrentHashMap<>();

    private Session session;

    private static CopyOnWriteArraySet<WebSocketServer> webSockets =new CopyOnWriteArraySet<>();

    public static Map<String,Session> sessionPool = new ConcurrentHashMap<String,Session>();


    @OnOpen
    public void onOpen(Session session, @PathParam(value="userId")String userId) {
        this.session = session;
        webSockets.add(this);
        sessionPool.put(userId, session);
        log.info("【websocket消息】有新的连接:"+userId+"，总数为:"+webSockets.size());
        stringRedisTemplate.opsForValue().set("ws_online:"+userId,"在线");
        long size = stringRedisTemplate.opsForList().size("Msg_userId:"+userId);
        if(size>0){
            sendOneMessage(userId,size+"");
        }
    }

    @OnClose
    public void onClose() {
        webSockets.remove(this);
        //webSocket移除用户
        String userId = this.session.getPathParameters().get("userId");
        sessionPool.remove(userId);
        stringRedisTemplate.delete("ws_online:"+userId);
        log.info("【websocket消息】连接断开，总数为:"+webSockets.size());
    }

    @OnMessage
    public void onMessage(String message) {
         log.info("【websocket消息】收到客户端消息:"+message);
         //心跳机制
//        if (StringUtils.isEmpty(message)) {
//            return;
//        }
//        WSDto heartBeatDTO = JSON.parseObject(message, WSDto.class);
//        if(StringUtils.equals(heartBeatDTO.getType(),"wsHeart")){//如果是心跳、用户续命5s
//            Long currentDate = System.currentTimeMillis();
//            Long expireTime = currentDate + 5 * 1000;
//            String userId = heartBeatDTO.getUserId();
//            onLineUser.put(userId, expireTime);
//        }
    }

    /**
     * 移除过期用户,如果用户超过5s未获取到心跳列表则清除在线用户信息（ws服务端删除，而不是vue前端调用websocket.close()）
     * 需要app开启：@EnableScheduling
     */
    @Scheduled(cron = "0/5 * * * * ?")
    private void removeOnLineUser() {
        Long currentDate = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> it = onLineUser.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> entry = it.next();
            String key = entry.getKey();
            Long value = entry.getValue();
            Long userExpireTime = value + 5 * 1000;
            if (currentDate > userExpireTime) {
                log.info("心跳超时,用户下线:"+key);
                onLineUser.remove(key);
                sessionPool.remove(key);
            }
        }
    }

    // 此为广播消息
    public void sendAllMessage(String message) {
        for(WebSocketServer webSocket : webSockets) {
             log.info("【websocket消息】广播消息:"+message);
            try {
                webSocket.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息
    public void sendOneMessage(String userId, String message) {
         log.info("【websocket消息】单点消息:"+message);
        Session session = sessionPool.get(userId);
        if (session != null) {
            log.info("用户【"+userId+"】收到消息："+message);
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        WebSocketServer.stringRedisTemplate = stringRedisTemplate;
    }

}