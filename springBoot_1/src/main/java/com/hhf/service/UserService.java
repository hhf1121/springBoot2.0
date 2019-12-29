package com.hhf.service;

/**
 * 开启事务、方法上加@Transactional
 * SpringBoot2.0整合pagehelper
 */

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhf.utils.ResultUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
import com.hhf.entity.User;
import com.hhf.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

@Slf4j
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

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
    public int insertUser(String userName, String passWord) {
        User user = new User();
        user.setUserName(userName);
        user.setPassWord(passWord);
        int insert = userMapper.insert(user);
        log.info("----------insertUser:service-----------");
        int i = 1 / Integer.parseInt(passWord);
        return insert;
    }

//	public PageInfo<User> query(String name,int pageNum,int pageSize) {
//		log.info("----------queryUserByPage:service-----------");
//		//使用pagehelper:生产page信息
//		PageHelper.startPage(pageNum, pageSize);//底层：改写（拼接）sql
//		List<User> findByName = JSONObject.parseArray(loadCacheRedis(name), User.class);
//		PageInfo<User> result=new PageInfo<User>(findByName);
//		return result;
//	}

    /**
     * 读取redis缓存，使用redis分布式锁
     *
     * @param name
     * @return
     */
    private String loadCacheRedis(String name) {
        String usekey = stringRedisTemplate.opsForValue().get(name);
        String clientId = UUID.randomUUID().toString();
        //为了防止锁块里的代码执行时间超出设置时长，导致解锁混乱。使用uuid，只能自己解自己的锁。
        //在finally块中判断，value是否和clientId相等，然后再解锁。
        if (null != usekey) {//redis缓存
            log.info("读取redis缓存......");
            return usekey;
        } else {
            //setIfAbsent,底层还是jedis的setNx
            if (stringRedisTemplate.opsForValue().setIfAbsent("lock", clientId)) {//拿到redis分布锁，去mysql查询
                log.info("拿到锁了，去DB中查询...");//可能在这里挂掉了。
                stringRedisTemplate.expire("lock", 5, TimeUnit.SECONDS);//凭借经验设置自动失效时间，但存在问题。可能没有执行完，锁就失效。
//				stringRedisTemplate.opsForValue().setIfAbsent("lock","up",5,TimeUnit.SECONDS);//高版本的redis，可同时设置超时时间。
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.like("name", name);
                List<User> findByName = userMapper.selectList(wrapper);
                String value = JSON.toJSONString(findByName);
                stringRedisTemplate.opsForValue().set(name, value);//放入Redis
                stringRedisTemplate.expire(name, 600, TimeUnit.SECONDS);//设置失效时间
                stringRedisTemplate.delete("lock");//释放掉锁：遇到异常的时候，锁无法释放。
                return value;
            } else {//没拿到锁，休眠200ms，再去拿。防止大量sql同时怼到db。 造成，缓存击穿
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
     * 加锁后，想再优化性能，可把库存拆分成多段，放入DB中，自己玩自己的。
     * 如果某一个分段库存用完，怎么解决？
     * 如果是redis主从，怎么解决主从同步？考虑用zk替换redis
     *
     * @param name
     * @return
     */
    private String loadCacheRedisByRedisson(String name) {
        String lock = "redissonKey";
        RLock rlock = redisson.getLock(lock);
        String usekey = stringRedisTemplate.opsForValue().get(name);
        if (null != usekey) {//redis缓存
            log.info("读取redis缓存......");
            return usekey;
        } else {//同一时刻，只能有一个对象去DB中查询，防止缓存击穿
            String value = "";
            try {
                rlock.lock();//上锁           原理：redis上锁的时候，启动一个守护线程，定时去检测key是否存在，如果存在，就重新设置失效时间。(续命锁)
//                rlock.tryLock(30,TimeUnit.SECONDS);//同上，指定时间
                log.info("redisson拿到了锁，去DB中查询...");
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.like("name", name);
                List<User> findByName = userMapper.selectList(wrapper);
                value = JSON.toJSONString(findByName);
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
     *
     * @param user
     * @return
     */
    public List<User> queryVIP(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("yes", user.getYes());
        return userMapper.selectList(wrapper);
    }


    public int insertDataByVue(User user) {
        return userMapper.insert(user);
    }

    public int updateDataByVue(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id", user.getId());
        return userMapper.update(user, wrapper);
    }

    public User queryByVue(String userName, String passWord) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("userName", userName).eq("passWord", passWord);
        return userMapper.selectOne(wrapper);
    }

    public int deleteByVue(Long id) {
        return userMapper.deleteById(id);
    }

    public Map<String, Object> queryPage(User user) {
        //1.MP插件
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (user.getYes() != null) wrapper.eq("yes", user.getYes());
        if (user.getCreateDate() != null) wrapper.eq("createDate", user.getCreateDate());
        IPage<User> page = new Page(user.getPageIndex(), user.getPageSize());
        IPage<User> iPage = userMapper.selectPage(page, wrapper);
        //2.手写分页
//        user.setPageIndex((user.getPageIndex()-1)*user.getPageSize());
//        List<User> list=userMapper.selectPage(user);
//        Long count=userMapper.selectCount(user);
//        Page<User> page=new Page<>();
//        page.setRecords(list);
//        page.setTotal((long) count);
//        page.setCurrent(user.getPageIndex());
//        page.setSize(user.getPageSize());
        return ResultUtils.getSuccessResult(iPage);
    }

    public User getCurrentUserByid(String id) {
        return userMapper.selectById(id);
    }
}
