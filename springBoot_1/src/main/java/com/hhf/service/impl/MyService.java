package com.hhf.service.impl;


//import com.hhf.api.providerApi;

import com.hhf.mapper.UserMapper;
import com.hhf.service.IMyService;
import com.hhf.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MyService implements IMyService {

    //本地资源管理器
    @Autowired
    private UserMapper userMapper;

    //rpc调用-远程资源管理器
//    @Autowired
//    private providerApi providerApi;


//    @Transactional//开启spring本地事务，解决不了分布式事务（fegin客户端调用）
//    @GlobalTransactional(name = "spring-seata-tranaction",rollbackFor = Exception.class)//分布式事务
    @Override
    public Map<String, Object> isTranaction(Long id) {
        //操作user
        userMapper.deleteById(id);
        //操作book
//        providerApi.updateCount(id);
        //制造异常
        int i=1/0;
        return ResultUtils.getSuccessResult("操作成功");
    }
}
