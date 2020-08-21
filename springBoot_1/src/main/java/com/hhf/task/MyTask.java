//package com.hhf.task;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.dangdang.ddframe.job.api.ShardingContext;
//import com.dangdang.ddframe.job.api.simple.SimpleJob;
//import com.hhf.entity.User;
//import com.hhf.service.UserService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * 简单任务
// */
//@Component
//@Slf4j
//public class MyTask implements SimpleJob {
//
//    @Autowired
//    private UserService userService;
//
//
//    @Override
//    public void execute(ShardingContext shardingContext) {
//        log.info("分片:"+shardingContext.getShardingItem());
//        log.info("此分片参数:"+shardingContext.getShardingParameter());
//        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
//        if(StringUtils.isNotEmpty(shardingContext.getShardingParameter())){
//            queryWrapper.eq("yes",shardingContext.getShardingParameter());
//        }
//        List<User> list = userService.list(queryWrapper);
//        list.forEach(o->{
//            log.info("分片:"+shardingContext.getShardingItem()+"->"+o.toString());
//        });
//    }
//
//}
