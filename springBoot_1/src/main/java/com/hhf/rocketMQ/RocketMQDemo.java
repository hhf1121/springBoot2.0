package com.hhf.rocketMQ;

import com.alibaba.dubbo.remoting.RemotingException;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class RocketMQDemo {

    static final String MQ_NAMESRVADDR = "127.0.0.1:9876";

    public static void main(String[] args) {
        // 分组名
        String groupName = "myGroup-";
        // 主题名
        String topicName = "myTopic";
        // 标签名
        String tagName = "myTag-1";
        for (int i=0;i<10;i++){
            int j=i;
            new Thread(() -> {

                try {
                    producer(groupName+j, topicName, tagName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RemotingException e) {
                    e.printStackTrace();
                } catch (MQClientException e) {
                    e.printStackTrace();
                } catch (MQBrokerException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        //消费者
        new Thread(() -> {
            try {
                consumer(groupName, topicName, tagName);
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * @Description 生产者
     * @Author wanglei
     * @Param [groupName 分组名, topicName 主题名, tagName 标签名]
     **/
    public static void producer(String groupName, String topicName, String tagName) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer(groupName);
        producer.setNamesrvAddr(MQ_NAMESRVADDR);
        producer.start();
        String body = "Hello, myMQ";
        Message message = new Message(topicName, tagName, body.getBytes());
        try {
            producer.send(message);
        } catch (org.apache.rocketmq.remoting.exception.RemotingException e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }

    /**
     * @Description 消费者
     * @Author wanglei
     * @Param [groupName 分组名, topicName 主题名, tagName 标签名]
     **/
    public static void consumer(String groupName, String topicName, String tagName) throws MQClientException {
        DefaultMQPushConsumer consumer;
        consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(MQ_NAMESRVADDR);
        consumer.subscribe(topicName, tagName);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(
                    List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.println(new String(msg.getBody()));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }
}