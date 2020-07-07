package com.hhf.webSocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ServerEndpoint("/msgWebSocket/{userId}")
//此注解相当于设置访问URL
public class WebSocketServer {

    /**
     * 无法注入,在启动类里手动注入
     */
    private static StringRedisTemplate stringRedisTemplate;

    private Session session;

    private static CopyOnWriteArraySet<WebSocketServer> webSockets =new CopyOnWriteArraySet<>();
    private static Map<String,Session> sessionPool = new HashMap<String,Session>();


    @OnOpen
    public void onOpen(Session session, @PathParam(value="userId")String userId) {
        this.session = session;
        webSockets.add(this);
        sessionPool.put(userId, session);
        log.info(userId+"【websocket消息】有新的连接，总数为:"+webSockets.size());

        long size = stringRedisTemplate.opsForList().size("Msg_userId:"+userId);
        if(size>0){
            sendOneMessage(userId,size+"");
        }
    }

    @OnClose
    public void onClose() {
        webSockets.remove(this);
        log.info("【websocket消息】连接断开，总数为:"+webSockets.size());
    }

    @OnMessage
    public void onMessage(String message) {
         log.info("【websocket消息】收到客户端消息:"+message);
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