package com.hhf.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hhf.service.UserService;
import com.hhf.utils.ResultUtils;

/**
 * userController
 * 集成mybatis、actuator 监控中心、PageHelper
 * @author Administrator
 *
 */

@RestController
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserService userService;
	
	
	//分页
	@RequestMapping("/query")
	public Map<String,Object> query(String name,int pageNum,int pageSize){
		return ResultUtils.getSuccessResult(userService.query(name,pageNum,pageSize));
	}
	
	@RequestMapping("/queryVIP")
	public Map<String,Object> queryUserByType(Integer yes){
		return ResultUtils.getSuccessResult(userService.queryVIP(yes));
	}
	
	@RequestMapping("/insertUser")
	public Map<String,Object> insertUser(String userName,String passWord){
		return ResultUtils.getSuccessResult(userService.insertUser(userName, passWord));
	} 
	
	@RequestMapping("/actuator")
	public Map<String,Object> actuator(){
		return ResultUtils.getSuccessResult("actuator");
	} 
	
}
