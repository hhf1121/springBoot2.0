package com.hhf.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hhf.entity.User;
import com.hhf.service.UserService;
import com.hhf.utils.ResultUtils;
import org.elasticsearch.rest.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 云路供应链科技有限公司 版权所有 © Copyright 2020
 *
 * @author hehongfei
 * @Description:
 * @date 2021-08-31
 */

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/registUser",method = RequestMethod.POST)
    public Map<String,Object> registUser(@RequestBody User user){
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<User>();
        queryWrapper.eq(User::getIsDelete,0);
        if(!StringUtils.isEmpty(user.getOpenId())){
            queryWrapper.eq(User::getOpenId,user.getOpenId());
        }
        if(!StringUtils.isEmpty(user.getUnionId())){
            queryWrapper.eq(User::getUnionId,user.getUnionId());
        }
        User one = userService.getOne(queryWrapper);
        if(one==null){
            if(userService.save(user)){
                return  ResultUtils.getSuccessResult(user);
            }else {
               throw new RuntimeException("注册用户失败");
            }
        }else {
            throw new RuntimeException("用户存在");
        }
    }


    @RequestMapping(value = "/loginUser",method = RequestMethod.POST)
    public Map<String,Object> loginUser(@RequestBody User user){
        User user1 = userService.loginByWx(user);
        return  ResultUtils.getSuccessResult(user1);
    }

}
