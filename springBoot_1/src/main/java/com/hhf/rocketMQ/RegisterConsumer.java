package com.hhf.rocketMQ;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.hhf.entity.BaseMsg;
import com.hhf.entity.User;
import com.hhf.mapper.BaseMsgMapper;
import com.hhf.mapper.UserMapper;
import com.hhf.utils.CurrentUserContext;
import com.hhf.utils.SnowflakeIdWorker;
import com.hhf.vo.RegisterMQVo;
import com.hhf.webSocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;


@Slf4j
@Component
@Order(2)
public class RegisterConsumer implements CommandLineRunner {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private WebSocketServer webSocketServer;

    private SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
    /**
     * NameServer 地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    DefaultMQPushConsumer consumer=new DefaultMQPushConsumer("registerGroup");

    /**
     * 初始化RocketMq的监听信息，渠道信息
     */
    public void messageListener(){

        consumer.setNamesrvAddr(namesrvAddr);
        try {

            //订阅某Topic下所有类型的消息，Tag用符号 * 表示:consumer.subscribe("MQ_TOPIC", "*", new MessageListener() {……});
            //订阅某Topic下某一种类型的消息，请明确标明Tag：consumer.subscribe("MQ_TOPIC", "TagA", new MessageListener() {……});
            //订阅某Topic下多种类型的消息，请在多个Tag之间用 || 分隔:consumer.subscribe("MQ_TOPIC", "TagA||TagB", new MessageListener() {……});
            // 订阅PushTopic下Tag为push的消息,都订阅消息
            consumer.subscribe("registerTopic", "registerUserTag");
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
                        //存入redis(更新redis)
                        long size=stringRedisTemplate.opsForList().size("Msg_userId:"+vo.getToId());
                        if(size>0L){
                            BaseMsg msg1 = new BaseMsg();
                            msg1.setFromId(Integer.parseInt(vo.getFromId()));
                            msg1.setToId(Integer.parseInt(vo.getToId()));
                            msg1.setMsg(vo.getMsg());
                            String time = new Date().getTime()+"";
                            long now = Long.parseLong(time.substring(0, time.length() - 3) + "000");
                            msg1.setLastTime(new Date(now));
                            msg1.setSign(idWorker.nextId()+"");
                            Object jsonObj= JSON.toJSONString(msg1, SerializerFeature.WriteMapNullValue);
                            stringRedisTemplate.opsForList().leftPush("Msg_userId:"+vo.getToId(),jsonObj.toString());//存入redis
                            size=size+1;
                        }else{
                            BaseMsg baseMsg = new BaseMsg();
                            baseMsg.setFromId(Integer.parseInt(vo.getFromId()));
                            baseMsg.setToId(Integer.parseInt(vo.getToId()));
                            baseMsg.setMsg(vo.getMsg());
                            String time = new Date().getTime()+"";
                            long now = Long.parseLong(time.substring(0, time.length() - 3) + "000");
                            baseMsg.setLastTime(new Date(now));
                            baseMsg.setSign(idWorker.nextId()+"");
                            Object jsonObj= JSON.toJSONString(baseMsg, SerializerFeature.WriteMapNullValue);
                            stringRedisTemplate.opsForList().leftPush("Msg_userId:"+vo.getToId(),jsonObj.toString());//存入redis
                            size=1;
                        }
                        //webSocket发送信息
                        webSocketServer.sendOneMessage(vo.getToId(),size+"");
                        log.info("消息mq转储到redis成功...");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            consumer.start();
            log.info("registerConsumer,启动成功...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopListener(){
        consumer.shutdown();
        log.info("registerConsumer,停止成功...");
    }

    @Override
    public void run(String... args) throws Exception {
        this.messageListener();
    }

}
