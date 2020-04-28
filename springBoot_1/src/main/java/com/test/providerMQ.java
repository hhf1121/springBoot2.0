package com.test;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;

public class providerMQ {

    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException, MQBrokerException {
        DefaultMQProducer defaultMQProducer=new DefaultMQProducer("myproducergroup");
        defaultMQProducer.setNamesrvAddr("192.168.202.128:9876;192.168.202.129:9876");
        defaultMQProducer.setSendMsgTimeout(10000);
        defaultMQProducer.start();
        int i=0;
        for (; ;) {
            Message message = new Message("mytesttoptic", "istag", "key", ("hello!mq!" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            defaultMQProducer.send(message);
            i++;
        }
//        defaultMQProducer.shutdown();
    }
}
