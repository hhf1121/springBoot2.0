package com.hhf.controller;

import com.hhf.rocketMQ.MQProducer;
import com.hhf.utils.ResultUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * RocketMQ
 *
 */

@RestController
@RequestMapping("mq")
public class MQController {

    @Autowired
    private MQProducer mqProducer;

    @RequestMapping("/producer")
    public Map<String,Object> callback(String message) {
        try {
           return mqProducer.send("PushTopic","push",message);
        } catch (UnsupportedEncodingException e) {
            return ResultUtils.getFailResult(e.getMessage());
        } catch (InterruptedException e) {
            return ResultUtils.getFailResult(e.getMessage());
        } catch (RemotingException e) {
            return ResultUtils.getFailResult(e.getMessage());
        } catch (MQClientException e) {
            return ResultUtils.getFailResult(e.getMessage());
        } catch (MQBrokerException e) {
            return ResultUtils.getFailResult(e.getMessage());
        }
    }

}
