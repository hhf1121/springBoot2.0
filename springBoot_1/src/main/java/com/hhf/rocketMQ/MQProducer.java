package com.hhf.rocketMQ;

import com.hhf.utils.ResultUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.Map;


@Component
public class MQProducer{

        // 生产者的组名
        @Value("${apache.rocketmq.producer.producerGroup}")
        private String producerGroup;

        private DefaultMQProducer producer;
        /**
         * NameServer 地址
         */
        @Value("${apache.rocketmq.namesrvAddr}")
        private String namesrvAddr;

        /*
            @PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器调用一次，类似于Serclet的inti()方法。
             被@PostConstruct修饰的方法会在构造函数之后，init()方法之前运行
         */
        @PostConstruct
        public void defaultMQProducer() {
            //生产者的组名
            producer= new DefaultMQProducer(producerGroup);
            //指定NameServer地址，多个地址以 ; 隔开
            producer.setNamesrvAddr(namesrvAddr);
            producer.setVipChannelEnabled(false);
//            producer.createTopic();
            try {
                producer.start();
                System.out.println("-------->:producer启动了");
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        }

        public Map<String, Object> send(String topic, String tags, String body) throws InterruptedException, RemotingException, MQClientException, MQBrokerException, UnsupportedEncodingException {
            Message message = new Message(topic, tags, body.getBytes(RemotingHelper.DEFAULT_CHARSET));
            StopWatch stop = new StopWatch();
            stop.start();
            SendResult result = producer.send(message);
            System.out.println("发送响应：MsgId:" + result.getMsgId() + "，发送状态:" + result.getSendStatus());
            stop.stop();
            return ResultUtils.getSuccessResult(result);
        }
}