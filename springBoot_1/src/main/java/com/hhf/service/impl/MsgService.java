package com.hhf.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hhf.entity.BaseMsg;
import com.hhf.entity.User;
import com.hhf.feignClient.FeignHttpServer;
import com.hhf.feignClient.PortalAgencyCenterDto;
import com.hhf.mapper.BaseMsgMapper;
import com.hhf.service.IMsgService;
import com.hhf.service.UserService;
import com.hhf.utils.CurrentUserContext;
import com.hhf.utils.ResultUtils;
import com.hhf.utils.SnowflakeIdWorker;
import com.hhf.vo.MsgVo;
import com.hhf.vo.NotificationUserMQVo;
import com.hhf.vo.RegisterMQVo;
import com.hhf.webSocket.WebSocketServer;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MsgService extends ServiceImpl<BaseMsgMapper,BaseMsg> implements IMsgService {

    @Autowired
    private BaseMsgMapper baseMsgMapper;
    @Autowired
    private UserService userService;

    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    //生产者的组名
    private DefaultMQProducer producer= null;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WebSocketServer webSocketServer;

    private SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

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
        User currentUser = CurrentUserContext.getCurrentUser();
        Integer currentId = currentUser.getId();
        IPage page=new Page(baseMsg.getPageIndex(),baseMsg.getPageSize());
        QueryWrapper<BaseMsg> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_name", currentUser.getUserName());
        if(baseMsg.getStatus()!=null&&baseMsg.getStatus()==0){//未读--》redis
            return getPageByRedis(baseMsg,currentUser.getId());
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
            record.setIdstr(record.getId()+"");
        }
        return iPage;
    }

    private IPage<BaseMsg> getPageByRedis(BaseMsg baseMsg, Integer id) {
        Page<BaseMsg> resultPage = new Page<BaseMsg>();
        List<String> range = redisTemplate.opsForList().range("Msg_userId:"+id, 0, -1);
        if(range.isEmpty())return new Page<>();
        List<BaseMsg> baseMsgs=Lists.newArrayList();
        for (String s : range) {
            JSONObject jsonObject = JSONObject.parseObject(s);
            BaseMsg baseMsg1 = JSONObject.toJavaObject(jsonObject, BaseMsg.class);
            baseMsgs.add(baseMsg1);
        }
        List<BaseMsg> baseMsgs1 =Lists.newArrayList();
        Integer pageSize = baseMsg.getPageSize();//页面容量
        Integer pageIndex = baseMsg.getPageIndex();//当前页
        int start=(pageIndex-1)*pageSize;
        int totalPage=baseMsgs.size()%pageSize>0?baseMsgs.size()/pageSize+1:baseMsgs.size()/pageSize;
        if(pageIndex<=totalPage){
            baseMsgs1 = baseMsgs.subList(start, pageSize * pageIndex+1>baseMsgs.size()?baseMsgs.size():pageSize * pageIndex+1);
        }
        if (baseMsgs1.size()>pageSize){
            resultPage.setSearchCount(true);
            baseMsgs1.remove(baseMsgs1.size()-1);
        }else {
            resultPage.setSearchCount(false);
        }
        Map<String,String> userMaps= Maps.newHashMap();
        for (User user : UserService.cacheUser) {
            userMaps.put(user.getId()+"",user.getName());
        }
        for (BaseMsg record : baseMsgs1) {
            record.setFromName(userMaps.get(record.getFromId()+""));
            record.setToName(userMaps.get(record.getToId()+""));
        }
        resultPage.setRecords(baseMsgs1);
        resultPage.setTotal(baseMsgs.size());
        resultPage.setSize(baseMsg.getPageSize());
        resultPage.setCurrent(baseMsg.getPageIndex());
        return resultPage;
    }

    @Override
    public Map<String, Object> getMsgCount() {
        QueryWrapper<BaseMsg> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("to_id", CurrentUserContext.getCurrentUser().getId())
        .eq("status",0);
        return ResultUtils.getSuccessResult(baseMsgMapper.selectCount(queryWrapper));
    }

    @Override
    public Map<String, Object> signRead(MsgVo baseMsg) {
        User currentUser = CurrentUserContext.getCurrentUser();
        List<BaseMsg> baseMsgList = baseMsg.getBaseMsgList();
        //当前操作的数据,转换成jsonString、然后删除redis里的
        List<String> sgins=Lists.newArrayList();
        for (BaseMsg msg : baseMsgList) {
            setDefaultValue(msg,currentUser);
            sgins.add(msg.getSign());
        }
        //guava处理list、切分小list
        List<List<BaseMsg>> partition = Lists.partition(baseMsgList, 500);
        boolean flag=false;
        for (List<BaseMsg> baseMsgs : partition) {
            flag=saveBatch(baseMsgs);
        }
        if(flag){
            //更新redis的list，删除入库的未读。
            int deleteCount=0;
            for (BaseMsg msg : baseMsgList) {
                BaseMsg deleteMsg = new BaseMsg();
                deleteMsg.setFromId(msg.getFromId());
                deleteMsg.setToId(msg.getToId());
                deleteMsg.setMsg(msg.getMsg());
                deleteMsg.setLastTime(msg.getLastTime());
                deleteMsg.setSign(msg.getSign());
                Object jsonObj= JSON.toJSONString(deleteMsg, SerializerFeature.WriteMapNullValue);
                deleteCount+=redisTemplate.opsForList().remove("Msg_userId:"+currentUser.getId(),0,jsonObj.toString());
            }
            Long size = redisTemplate.opsForList().size("Msg_userId:" + currentUser.getId());
            //webSocket发送信息
            webSocketServer.sendOneMessage(currentUser.getId()+"",size+"");
            return ResultUtils.getSuccessResult("已标记为已读");
        }
        return  ResultUtils.getFailResult("标记失败");
    }

    private void setDefaultValue(BaseMsg msg, User currentUser) {
        if(StringUtils.isBlank(msg.getUserName()))msg.setUserName(currentUser.getUserName());
        msg.setIsDelete(0);
        msg.setStatus(1);
    }


    @Override
    public Map<String, Object> deleteMsgs(MsgVo baseMsg) {
        User currentUser = CurrentUserContext.getCurrentUser();
        List<BaseMsg> baseMsgList = baseMsg.getBaseMsgList();
        int update=0;
        if(!baseMsgList.isEmpty()){
            if(StringUtils.isBlank(baseMsgList.get(0).getSign())){ //已读、已发
                List<String> ids = Lists.newArrayList();
                for (BaseMsg msg : baseMsgList) {
                    ids.add(msg.getIdstr());
                }
                UpdateWrapper updateWrapper=new UpdateWrapper();
                updateWrapper.set("is_delete",1);
                updateWrapper.in("id",ids);
                update = baseMsgMapper.update(new BaseMsg(), updateWrapper);
            }else {//redis-->删除未读
                int deleteCount=0;
                for (BaseMsg msg : baseMsgList) {
                    BaseMsg deleteMsg = new BaseMsg();
                    deleteMsg.setFromId(msg.getFromId());
                    deleteMsg.setToId(msg.getToId());
                    deleteMsg.setMsg(msg.getMsg());
                    deleteMsg.setLastTime(msg.getLastTime());
                    deleteMsg.setUserName(msg.getUserName());
                    deleteMsg.setSign(msg.getSign());
                    Object jsonObj= JSON.toJSONString(deleteMsg, SerializerFeature.WriteMapNullValue);
//                    List<String> range = redisTemplate.opsForList().range("Msg_userId:" + currentUser.getId(), 0, -1);
//                    range.forEach(o->{
//                        System.out.println(o);
//                    });
                    deleteCount += redisTemplate.opsForList().remove("Msg_userId:"+currentUser.getId(), 0, jsonObj.toString());
                }
                Long size = redisTemplate.opsForList().size("Msg_userId:" + currentUser.getId());
                update=deleteCount;
                webSocketServer.sendOneMessage(baseMsgList.get(0).getToId()+"",size+"");
            }
        }
        if(update>0){
            return ResultUtils.getSuccessResult("删除成功");
        }
        return  ResultUtils.getFailResult("删除失败");
    }

    @Autowired
    private FeignHttpServer feignHttpServer;

    @Override
    public Map<String, Object> sendMsg(BaseMsg baseMsg, HttpServletRequest request) {
        //MQVo
        RegisterMQVo vo=new RegisterMQVo();
        vo.setFromId(baseMsg.getFromId()+"");
        vo.setMsg(baseMsg.getMsg());
        vo.setToId(baseMsg.getToId()+"");
        //存入redis(更新redis)  改为list存储
        long countSize=redisTemplate.opsForList().size("Msg_userId:"+vo.getToId());
        if(countSize>0L){
            BaseMsg baseMsg1 = new BaseMsg();
            baseMsg1.setFromId(Integer.parseInt(vo.getFromId()));
            baseMsg1.setToId(Integer.parseInt(vo.getToId()));
            baseMsg1.setMsg(vo.getMsg());
            String time = new Date().getTime()+"";
            long now = Long.parseLong(time.substring(0, time.length() - 3) + "000");
            baseMsg.setLastTime(new Date(now));
            baseMsg.setSign(idWorker.nextId()+"");
            Object jsonObj= JSON.toJSONString(baseMsg, SerializerFeature.WriteMapNullValue);
            redisTemplate.opsForList().leftPush("Msg_userId:"+vo.getToId(),jsonObj.toString());//存入redis
            countSize=countSize+1;
        }else{
            BaseMsg baseMsg1 = new BaseMsg();
            baseMsg1.setFromId(Integer.parseInt(vo.getFromId()));
            baseMsg1.setToId(Integer.parseInt(vo.getToId()));
            baseMsg1.setMsg(vo.getMsg());
            String time = new Date().getTime()+"";
            long now = Long.parseLong(time.substring(0, time.length() - 3) + "000");
            baseMsg.setLastTime(new Date(now));
            baseMsg.setSign(idWorker.nextId()+"");
            Object jsonObj= JSON.toJSONString(baseMsg, SerializerFeature.WriteMapNullValue);
            redisTemplate.opsForList().leftPush("Msg_userId:"+vo.getToId(),jsonObj.toString());//存入redis
            countSize=1;
        }
        vo.setCount(countSize+"");
        Object jsonObj= JSON.toJSONString(vo, SerializerFeature.WriteMapNullValue);
        try {
            Message message = new Message("msgTopic", "msgTag", jsonObj.toString().getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = producer.send(message);
            //保存作为发件方的消息记录
            int i = baseMsgMapper.insertSelective(baseMsg);
            //推送给mh系统（调接口）
//            try {
//                PortalAgencyCenterDto dto=new PortalAgencyCenterDto();
//                dto.setAccessKey("1ZvivWa6I0I2i80GeqKRTc65Eptpg39M");
//                dto.setAcceptanceType(1);
//                dto.setSourceCode("HHF");
//                dto.setSourceType("1");
//                dto.setSourceSign("WEIYIBIANMA"+redisTemplate.opsForValue().increment("HHF"));//待办唯一码,根据此字段更新状态
//                dto.setAgentCodes(Lists.newArrayList("050069wode"));
//                dto.setAgencyTitle(baseMsg.getMsg());
//                dto.setAgencyType("1");//大类
//                dto.setAgencyCategory("测试类代办");//小类
//                dto.setCallbackUrl("http://192.168.202.53:8081/#/ChinaMap/about");
//                dto.setAcceptanceTime(new Date());
//                Map<String, Object> map = feignHttpServer.sendAgency(dto);
//                log.info(map.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            if(i<1){
                return ResultUtils.getFailResult("消息保存失败");
            }
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

    @Override
    public int saveOrUpdateMsgRedis(BaseMsg baseMsg) {
        baseMsg.setLastTime(new Date());
        //存入redis(更新redis)
        List<BaseMsg> msgRedisVos = Lists.newArrayList();
        String user = redisTemplate.opsForValue().get(baseMsg.getToId().toString());
        if(!org.springframework.util.StringUtils.isEmpty(user)){
            msgRedisVos = JSONArray.parseArray(user, BaseMsg.class);
            msgRedisVos.add(baseMsg);
            Object jsonObj= JSON.toJSONString(msgRedisVos, SerializerFeature.WriteMapNullValue);
            redisTemplate.opsForValue().set(baseMsg.getToId().toString(),jsonObj.toString());//存入redis
        }else{
            msgRedisVos = Lists.newArrayList();
            msgRedisVos.add(baseMsg);
            Object jsonObj= JSON.toJSONString(msgRedisVos, SerializerFeature.WriteMapNullValue);
            redisTemplate.opsForValue().set(baseMsg.getToId()+"",jsonObj.toString());//存入redis
        }
        return msgRedisVos.size();
    }

    @Override
    public int insertEntity(BaseMsg baseMsg) {
        return baseMsgMapper.insertSelective(baseMsg);
    }

    @Override
    public Map<String, Object> sendAllMsg(String msg) {
        NotificationUserMQVo vo=new NotificationUserMQVo();
        vo.setType("allUserMsg");
        vo.setUserIds(Lists.newArrayList());
        vo.setMsg(msg);
        Object jsonObj= JSON.toJSONString(vo, SerializerFeature.WriteMapNullValue);
        try {
            Message message = new Message("noticeTopic", "noticeTag", jsonObj.toString().getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = producer.send(message);
            log.info("MQ发送响应：MsgId:" + result.getMsgId() + "，发送状态:" + result.getSendStatus());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtils.getFailResult("发送失败");
        }
        return ResultUtils.getSuccessResult("发送成功");
    }


}
