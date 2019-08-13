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
import org.redisson.Redisson;
import org.redisson.api.RLock;
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
import redis.clients.jedis.JedisCluster;

@Slf4j
@Service
public class UserService /*extends CommonDao*/{
	
	@Autowired
	private UserMapper userMapper;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

//	@Autowired
//	RedisTemplate<String,String> redisTemplate;

//	@Autowired
//	private  JedisCluster jedisCluster;

	@Autowired(required = false)
	private Redisson redisson;
	
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
                stringRedisTemplate.expire("lock",5,TimeUnit.SECONDS);//凭借经验设置自动失效时间，但存在问题。可能没有执行完，锁就失效。
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				List<User> findByName = userMapper.findByName(name);
				String value = JSON.toJSONString(findByName);
				stringRedisTemplate.opsForValue().set(name,value);//放入Redis
				stringRedisTemplate.expire(name,600, TimeUnit.SECONDS);//设置失效时间
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
	 * 用redisson加锁
	 * @param name
	 * @return
	 */
	private String loadCacheRedisByRedisson(String name){
		String lock="redissonKey";
		RLock  rlock = redisson.getLock(lock);
		String usekey = stringRedisTemplate.opsForValue().get(name);
		if(null!= usekey){//redis缓存
			log.info("读取redis缓存......");
			return usekey;
		}else {//同一时刻，只能有一个对象去DB中查询，防止缓存击穿
			String value="";
			try{
				rlock.lock();//上锁           原理：redis上锁的时候，启动一个守护线程，定时去检测key是否存在，如果存在，就重新设置失效时间。
				log.info("redisson拿到了锁，去DB中查询...");
				List<User> findByName = userMapper.findByName(name);
				value= JSON.toJSONString(findByName);
				stringRedisTemplate.opsForValue().set(name, value);//放入Redis
				stringRedisTemplate.expire(name, 300, TimeUnit.SECONDS);//设置失效时间
				Thread.sleep(2000);//模拟时间
				return value;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				rlock.unlock();//解锁
			}
			try {
				log.info("没有拿到锁，等待200ms...");
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return loadCacheRedisByRedisson(name);//递归调用
		}
	}



	/**
	 * redis集群
	 * @param yes
	 * @return
	 */
	public List<User> queryVIP(Integer yes) {
//		jedisCluster.set("clusterKey","倚天屠龙记");
//		log.info(jedisCluster.get("clusterKey"));
		return userMapper.findByType(yes);
	}



	
}
