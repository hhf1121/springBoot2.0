package com.hhf.service;

/**
 * 开启事务、方法上加@Transactional
 * SpringBoot2.0整合pagehelper
 */

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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

	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
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
		List<User> findByName = JSONObject.parseArray(loadCacheRedis(name), User.class);
		PageInfo<User> result=new PageInfo<User>(findByName);
		return result;
	}

	/**
	 * 读取redis缓存，使用redis分布式锁
	 * @param name
	 * @return
	 */
	private String loadCacheRedis(String name) {
		String usekey = stringRedisTemplate.opsForValue().get(name);
		if(null!= usekey){//redis缓存
			log.info("读取redis缓存......");
			return usekey;
		}else{
			//setIfAbsent,底层还是jedis的setNx
			if(stringRedisTemplate.opsForValue().setIfAbsent("lock","up")){//拿到redis分布锁，去mysql查询
				log.info("拿到锁了，去DB中查询...");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				List<User> findByName = userMapper.findByName(name);
				String value = JSON.toJSONString(findByName);
				stringRedisTemplate.opsForValue().set(name,value);//放入Redis
				stringRedisTemplate.expire(name,10, TimeUnit.SECONDS);//设置失效时间
				stringRedisTemplate.delete("lock");//释放掉锁：遇到异常的时候，锁无法释放。
				return value;
			}else{//没拿到锁，休眠200ms，再去拿。防止大量sql同时怼到db。 造成，缓存击穿
				try {
					log.info("没有拿到锁，等待200ms...");
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return loadCacheRedis(name);//递归调用
			}
		}
	}


	/**
	 * redis集群
	 * @param yes
	 * @return
	 */
	public List<User> queryVIP(Integer yes) {
		stringRedisTemplate.opsForCluster();
		return userMapper.findByType(yes);
	}



	
}
