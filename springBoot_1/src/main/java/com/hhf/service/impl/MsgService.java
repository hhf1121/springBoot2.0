package com.hhf.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.hhf.entity.BaseMsg;
import com.hhf.entity.User;
import com.hhf.mapper.BaseMsgMapper;
import com.hhf.mapper.UserMapper;
import com.hhf.service.IMsgService;
import com.hhf.service.UserService;
import com.hhf.utils.CurrentUserContext;
import com.hhf.utils.ResultUtils;
import com.hhf.vo.RegisterMQVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MsgService implements IMsgService {

    @Autowired
    private BaseMsgMapper baseMsgMapper;
    @Autowired
    private UserService userService;

    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    //生产者的组名
    private DefaultMQProducer producer= null;

    @PostConstruct
    public void getMQ(){
        producer=new DefaultMQProducer("userMsgProducer");
        //指定NameServer地址，多个地址以 ; 隔开
        producer.setNamesrvAddr(namesrvAddr);
        try {
            producer.start();
            log.info("mq启动成功...");
        } catch (MQClientException e) {
            log.info(e.getMessage());
            log.info("mq启动失败...");
        }
    }

    @Override
    public IPage<BaseMsg> getMsg(BaseMsg baseMsg) {
        Integer currentId = CurrentUserContext.getCurrentUser().getId();
        IPage page=new Page(baseMsg.getPageIndex(),baseMsg.getPageSize());
        QueryWrapper<BaseMsg> queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq("to_id", currentId);
        if(baseMsg.getStatus()!=null){
            queryWrapper.eq("status", baseMsg.getStatus());
        }
        if(StringUtils.equals(baseMsg.getType(),"1")){//收件箱
            queryWrapper.eq("to_id",currentId);
        }
        if(StringUtils.equals(baseMsg.getType(),"2")){//发件箱
            queryWrapper.eq("from_id",currentId);
        }
        IPage iPage = baseMsgMapper.selectPage(page, queryWrapper);
        List<BaseMsg> records = iPage.getRecords();
        Map<String,String> userMaps= Maps.newHashMap();
        for (User user : UserService.cacheUser) {
            userMaps.put(user.getId()+"",user.getName());
        }
        for (BaseMsg record : records) {
            record.setFromName(userMaps.get(record.getFromId()+""));
            record.setToName(userMaps.get(record.getToId()+""));
        }
        return iPage;
    }

    @Override
    public Map<String, Object> getMsgCount() {
        QueryWrapper<BaseMsg> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("to_id", CurrentUserContext.getCurrentUser().getId())
        .eq("status",0);
        return ResultUtils.getSuccessResult(baseMsgMapper.selectCount(queryWrapper));
    }

    @Override
    public Map<String, Object> signRead(BaseMsg baseMsg) {
        List<String> ids = baseMsg.getIds();
        int update=0;
        if(!ids.isEmpty()){
//            BaseMsg baseMsg1=new BaseMsg();
//            baseMsg1.setStatus(1);//已读
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.set("status",1);
            updateWrapper.in("id",ids);
             update = baseMsgMapper.update(new BaseMsg(), updateWrapper);
        }
        if(update>0){
            return ResultUtils.getSuccessResult("已标记为已读");
        }
        return  ResultUtils.getFailResult("标记失败");
    }

    @Override
    public Map<String, Object> deleteMsgById(BaseMsg baseMsg) {
        List<String> ids = baseMsg.getIds();
        int update=0;
        if(!ids.isEmpty()){
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.set("is_delete",1);
            updateWrapper.in("id",ids);
            update = baseMsgMapper.update(new BaseMsg(), updateWrapper);
        }
        if(update>0){
            return ResultUtils.getSuccessResult("删除成功");
        }
        return  ResultUtils.getFailResult("删除失败");
    }

    @Override
    public Map<String, Object> sendMsg(BaseMsg baseMsg) {
        if(baseMsg.getId()!=null){//将此条信息标记为已读
            BaseMsg baseMsg1 = new BaseMsg();
            baseMsg1.setStatus(1);
            baseMsg1.setId(baseMsg.getId());
            baseMsgMapper.updateById(baseMsg1);
        }
        //MQVo
        RegisterMQVo vo=new RegisterMQVo();
        vo.setFromId(baseMsg.getFromId()+"");
        vo.setMsg(baseMsg.getMsg());
        vo.setToId(baseMsg.getToId()+"");
        Object jsonObj= JSON.toJSONString(vo, SerializerFeature.WriteMapNullValue);
        try {
            Message message = new Message("msgTopic", "msgTag", jsonObj.toString().getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = producer.send(message);
            log.info("发送响应：MsgId:" + result.getMsgId() + "，发送状态:" + result.getSendStatus());
        } catch (MQClientException e) {
            log.error(e.getErrorMessage());
            return ResultUtils.getFailResult("发送失败");
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            return ResultUtils.getFailResult("发送失败");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            return ResultUtils.getFailResult("发送失败");
        } catch (RemotingException e) {
            log.error(e.getMessage());
            return ResultUtils.getFailResult("发送失败");
        } catch (MQBrokerException e) {
            log.error(e.getErrorMessage());
            return ResultUtils.getFailResult("发送失败");
        }
        return ResultUtils.getSuccessResult("发送成功");
    }
}
