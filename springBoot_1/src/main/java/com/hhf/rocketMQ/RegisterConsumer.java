package com.hhf.rocketMQ;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hhf.entity.User;
import com.hhf.mapper.UserMapper;
import com.hhf.utils.CurrentUserContext;
import com.hhf.vo.RegisterMQVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;



@Slf4j
@Component
public class RegisterConsumer{

    /**
     * NameServer 地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    @Autowired
    private UserMapper userMapper;

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
            consumer.subscribe("registerTopic", "userByType3");
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
                        User user=new User();
                        user.setYes(2);
                        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
                        queryWrapper.eq("id",vo.getId());
                        userMapper.update(user,queryWrapper);
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

}
