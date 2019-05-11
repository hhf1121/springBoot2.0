package com.hhf.service;

/**
 * 开启事务、方法上加@Transactional
 * SpringBoot2.0整合pagehelper
 */

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hhf.entity.User;
import com.hhf.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService /*extends CommonDao*/{

	@Autowired
	private UserMapper userMapper;
	
	@Transactional
	public int insertUser(String userName,String passWord) {
		int insert = userMapper.insert(userName, passWord);
		log.info("----------insertUser:service-----------");
		int i=1/Integer.parseInt(passWord);
		return insert;
	}
	
	public PageInfo<User> query(String name,int pageNum,int pageSize) {
		log.info("----------queryUserByPage:service-----------");
		//使用pagehelper:生产page信息
		PageHelper.startPage(pageNum, pageSize);//底层：改写（拼接）sql
		List<User> findByName = userMapper.findByName(name);
		PageInfo<User> result=new PageInfo<User>(findByName);
		return result;
	}
	
	
	public List<User> queryVIP(Integer yes) {
		log.info("----------queryUserByType:service-----------");
		return userMapper.findByType(yes);
	}
}
