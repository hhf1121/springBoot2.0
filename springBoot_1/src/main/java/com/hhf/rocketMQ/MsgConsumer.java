package com.hhf.rocketMQ;

import com.alibaba.fastjson.JSONArray;
import com.hhf.vo.RegisterMQVo;
import com.hhf.webSocket.MsgWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


@Slf4j
@Component
@Order(3)
public class MsgConsumer implements CommandLineRunner {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    /**
     * NameServer 地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    @Autowired
    private MsgWebSocketServer webSocketServer;

    DefaultMQPushConsumer consumer=new DefaultMQPushConsumer("msgGroup");

    /**
     * 初始化RocketMq的监听信息，渠道信息
     */
    public void messageListener(){

        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setMessageModel(MessageModel.BROADCASTING);//广播
        try {
            //订阅某Topic下所有类型的消息，Tag用符号 * 表示:consumer.subscribe("MQ_TOPIC", "*", new MessageListener() {……});
            //订阅某Topic下某一种类型的消息，请明确标明Tag：consumer.subscribe("MQ_TOPIC", "TagA", new MessageListener() {……});
            //订阅某Topic下多种类型的消息，请在多个Tag之间用 || 分隔:consumer.subscribe("MQ_TOPIC", "TagA||TagB", new MessageListener() {……});
            // 订阅PushTopic下Tag为push的消息,都订阅消息
            consumer.subscribe("msgTopic", "msgTag");
            // 程序第一次启动从消息队列头获取数据
//            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            //可以修改每次消费消息的数量，默认设置是每次消费一条
//            consumer.setConsumeMessageBatchMaxSize(1);
            //在此监听中消费信息，并返回消费的状态信息
            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                // 会把不同的消息分别放置到不同的队列中
                for(Message msg:msgs){
                    try {
                        String info = new String(msg.getBody(), "utf-8");
                        log.info(getClass().getName()+"接收到了消息："+info);
                        RegisterMQVo vo = JSONArray.parseObject(info, RegisterMQVo.class);
                        //webSocket发送信息
                        webSocketServer.sendOneMessage(vo.getToId(),vo.getCount());
                        log.info("消息mq通知ws成功...");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    } catch (IOException e) {
                        e.printStackTrace();
                        log.error("webSocketServer.sendMessage失败！");
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            consumer.start();
            log.info("msgConsumer,启动成功...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopListener(){
        consumer.shutdown();
        log.info("msgConsumer,停止成功...");
    }

    @Override
    public void run(String... args) throws Exception {
        this.messageListener();
    }

}
