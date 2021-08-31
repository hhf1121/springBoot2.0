package com.hhf.service;

/**
 * 开启事务、方法上加@Transactional
 * SpringBoot2.0整合pagehelper
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.common.util.UuidUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.hhf.entity.BaseMsg;
import com.hhf.entity.User;
import com.hhf.entity.UserNote;
import com.hhf.enums.RedisKeyEnum;
import com.hhf.mapper.BaseMsgMapper;
import com.hhf.mapper.CommonMapper;
import com.hhf.mapper.UserMapper;
import com.hhf.mapper.UserNoteMapper;
import com.hhf.rocketMQ.RegisterConsumer;
import com.hhf.service.impl.UserNoteService;
import com.hhf.utils.CurrentUserContext;
import com.hhf.utils.JwtUtils;
import com.hhf.utils.ResultUtils;
import com.hhf.utils.VerifyCodeImgUtil;
import com.hhf.vo.NotificationUserMQVo;
import com.hhf.vo.RegisterMQVo;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;

@Slf4j
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements InitializingBean {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BaseMsgMapper baseMsgMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserNoteMapper userNoteMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private UserNoteService userNoteService;

    //获取application配置文件里面的值：初始化的时候
    @Value("${disconf.domainName}")
    private String domainName;


    public static List<User> cacheUser = Lists.newArrayList();

    //生产者的组名
    DefaultMQProducer producer = null;

    //声明一个布隆过滤器
    BloomFilter<Integer> integerBloomFilter = BloomFilter.create(Funnels.integerFunnel(), 100000, 0.01);


    @Autowired
    private RegisterConsumer registerConsumer;

//	@Autowired
//	RedisTemplate<String,String> redisTemplate;

//	@Autowired
//	private  JedisCluster jedisCluster;

    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    @Autowired(required = false)
    private Redisson redisson;

    @Value("${server.port}")
    private String port;

    @Autowired
    private IMsgService msgService;

    @PostConstruct
    public void getMQ() {
        producer = new DefaultMQProducer("registerMsgProducer");
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

    //使用PageInfo插件
//	public PageInfo<User> query(String name,int pageNum,int pageSize) {
//		log.info("----------queryUserByPage:service-----------");
//		//使用pagehelper:生产page信息
//		PageHelper.startPage(pageNum, pageSize);//底层：改写（拼接）sql
//		List<User> findByName = JSONObject.parseArray(loadCacheRedis(name), User.class);
//		PageInfo<User> result=new PageInfo<User>(findByName);
//		return result;
//	}

    public List<User> query(String name, int pageNum, int pageSize) {
        //1.使用布隆过滤器
        boolean b = integerBloomFilter.mightContain(name.hashCode());
        if (!b) {
            return Lists.newArrayList();
        }
        //2.查询redis
        List<User> findByName = JSONObject.parseArray(loadCacheRedis(name), User.class);
        return findByName;
    }

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
        if (userNoteService.districtMapCache.get(user.getAddress()) != null) {
            user.setAddress(userNoteService.districtMapCache.get(user.getAddress()));
        }
        int insert = userMapper.insert(user);
        //mp插件返回user的id
//        System.out.println(user.getId());
        if (insert > 0) {//重刷缓存、布隆过滤器
            initBloomFilter();
        }
        return insert;
    }

    public int updateDataByVue(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id", user.getId());
        int update = userMapper.update(user, wrapper);
        //用户更新成功、通知mq。再自我消费。通知发消息给当前用户、但未读的
        //MQVo
        QueryWrapper<BaseMsg> baseMsgQueryWrapper = new QueryWrapper<>();
        baseMsgQueryWrapper.select("from_id").eq("to_id", user.getId()).eq("status", 0);
        List<BaseMsg> baseMsgs = baseMsgMapper.selectList(baseMsgQueryWrapper);
        NotificationUserMQVo vo = new NotificationUserMQVo();
        vo.setType("noticeMsgUser");
        Set<Integer> collect = baseMsgs.stream().map(BaseMsg::getFromId).collect(Collectors.toSet());
        List<String> lists = Lists.newArrayList();
        for (Integer integer : collect) {
            lists.add(integer + "");
        }
        vo.setUserIds(lists);
        //发送mq通知用户
        new Thread(() -> {
            Object jsonObj = JSON.toJSONString(vo, SerializerFeature.WriteMapNullValue);
            try {
                Message message = new Message("noticeTopic", "noticeTag", jsonObj.toString().getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult result = producer.send(message);
                log.info("MQ发送响应：MsgId:" + result.getMsgId() + "，发送状态:" + result.getSendStatus());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }).start();
        return update;
    }

    public User queryByVue(String userName, String passWord, String verifyCode, HttpServletResponse httpServletResponse, HttpServletRequest request) throws IOException {
        //校验验证码：
        String s = stringRedisTemplate.opsForValue().get(userName);
        if (!verifyCode.equals(s)) {
            throw new RuntimeException("验证码不正确或已过期");
        }
        stringRedisTemplate.delete(userName);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("userName", userName).eq("passWord", passWord);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("账号或密码错误");
        }
        //保存到redis中，30分钟失效
        user.setToken(JwtUtils.generateById(user.getId()));
        Object jsonObj = JSON.toJSONString(user, SerializerFeature.WriteMapNullValue);
        stringRedisTemplate.opsForValue().set(RedisKeyEnum.USER.getCode()+user.getId()+"", jsonObj.toString(), 30, TimeUnit.MINUTES);
        return user;
    }


    public User loginByWx(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("openId", user.getOpenId());
        User one = userMapper.selectOne(wrapper);
        if (one == null) {
            throw new RuntimeException("不存在用户");
        }
        one.setIsDelete(0);
        userMapper.updateById(one);
        //保存到redis中，30分钟失效
        one.setToken(JwtUtils.generateById(one.getId()));
        Object jsonObj = JSON.toJSONString(one, SerializerFeature.WriteMapNullValue);
        stringRedisTemplate.opsForValue().set(RedisKeyEnum.USER.getCode()+one.getId()+"", jsonObj.toString(), 30, TimeUnit.MINUTES);
        
        return one;
    }

    public int deleteByVue(Long id) {
        return userMapper.deleteById(id);
    }


    public Map<String, Object> queryPage(User user) {
        //1.MP插件
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (user.getYes() != null) wrapper.eq("yes", user.getYes());
        if (user.getCreateDate() != null)
            wrapper.ge("createDate", user.getCreateDate()).le("createDate", new Date(user.getCreateDate().getTime() + 24 * 3600 * 1000 - 1));
        IPage<User> page = new Page(user.getPageIndex(), user.getPageSize());
        IPage<User> iPage = userMapper.selectPage(page, wrapper);
        List<User> records = iPage.getRecords();
        for (User record : records) {
//			String x=!StringUtils.isEmpty(record.getPicPath())? "http://"+getHostAddress()+":"+port+"/"+record.getPicPath():"";
//			record.setPicPath(x);
            if (!StringUtils.isEmpty(record.getPicPath())) log.info(record.getPicPath());
        }
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

    private String getHostAddress() {
        String hostAddress = null;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
            hostAddress = "localhost";
        }
        return hostAddress;
    }

    public User getCurrentUser(Long id) {
        User user = userMapper.selectById(id);
        //是否生日
        if (user != null && user.getBrithday() == null) {
            user.setIsBrithday("isError");
        } else {
            java.util.Date date = user.getBrithday();
            Instant instant = date.toInstant();
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
            LocalDate brithdayDate = localDateTime.toLocalDate();
            if (user != null && brithdayDate.compareTo(LocalDate.now()) == 0) {
                user.setIsBrithday("isBrithday");
            }
            if (user != null && brithdayDate.compareTo(LocalDate.now()) != 0) {
                user.setIsBrithday("isNoBrithday");
            }
        }
        //用户没有头像url，则用base64的头像
        if (user != null && !StringUtils.isEmpty(user.getUserName())) {//base64的做法
            byte[] bytes = user.getPhotoData();//转换成字节
            if (bytes != null && bytes.length > 0) {
                BASE64Encoder encoder = new BASE64Encoder();
                String png_base64 = encoder.encodeBuffer(bytes).trim();//转换成base64串
                png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
                user.setCachePhoto(png_base64);
            }
            user.setToken(JwtUtils.generateById(user.getId()));
            Object jsonObj = JSON.toJSONString(user, SerializerFeature.WriteMapNullValue);
            stringRedisTemplate.opsForValue().set(RedisKeyEnum.USER.getCode()+user.getId()+"", jsonObj.toString(), 30, TimeUnit.MINUTES);
            return user;
        }
        return new User();
    }

    public String getCurrentUserStr(Long id) {
        User user = userMapper.selectById(id);
        user.setToken(JwtUtils.generateById(user.getId()));
        Object jsonObj = JSON.toJSONString(user, SerializerFeature.WriteMapNullValue);
        stringRedisTemplate.opsForValue().set(RedisKeyEnum.USER.getCode()+user.getId()+"", jsonObj.toString(), 30, TimeUnit.MINUTES);
        if (user != null && !StringUtils.isEmpty(user.getUserName())) {
            return JSONObject.toJSONString(user);
        }
        return "未找到user";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //更新url为当前主机ip
        try {
//			updateCurrentIP();
        } catch (Exception e) {
            log.info("图片url解析失败..." + e.getMessage());
        }
        //初始化布隆过滤器
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                initBloomFilter();
            }
        };
        timer.scheduleAtFixedRate(task, 5000, 1000 * 30 * 60L);//延时5s、半小时刷新一次

        //定时清除已删除的文件
        Timer timer2 = new Timer();
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                clearFile();
            }
        };
        timer2.scheduleAtFixedRate(task2, 60000, 1000 * 60 * 60L);//延时1分钟、每1小时刷新一次

    }

    //改为域名的方式之后，该方法废弃
    private void updateCurrentIP() {
        //1.查询所有的picPath
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("picPath");
        queryWrapper.select("picPath");
        queryWrapper.last("limit 1");
        User user = userMapper.selectOne(queryWrapper);
        QueryWrapper<UserNote> userNoteQueryWrapper = new QueryWrapper<>();
        userNoteQueryWrapper.isNotNull("img_code");
        userNoteQueryWrapper.select("img_code");
        userNoteQueryWrapper.last("limit 1");
        UserNote userNotes = userNoteMapper.selectOne(userNoteQueryWrapper);
        String picPath = user.getPicPath().split(":")[1];//  http://192.168.202.53:8082/resources/static/file\1589005751161@头像1.png
        String userIp = picPath.replaceAll("/", "");
        String imgCode = userNotes.getImgCode().split(":")[1];//	http://192.168.202.53:8082/resources/static/file\1589006797861@vue-1.png;
        String noteIp = imgCode.replaceAll("/", "");
        //更新数据库里图片的ip地址为当前主机的ip
        Map<String, String> param = Maps.newHashMap();
        param.put("oldIP", userIp.equals(noteIp) ? userIp : "");
        param.put("newIP", getHostAddress());
        //更新db里图片的ip地址
        if (!param.get("oldIP").equals(param.get("newIP"))) {
            log.info("更新图片url的ip........");
            commonMapper.updateImgUrlIsCurrentIPByNote(param);
            commonMapper.updateImgUrlIsCurrentIPByUser(param);
        }
    }


    private void clearFile() {
        //1.查询所有的picPath
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("picPath");
        queryWrapper.select("picPath");
        List<User> users = userMapper.selectList(queryWrapper);
        Set<String> img = Sets.newHashSet();
        //1.user的文件
        //2.note的文件
        QueryWrapper<UserNote> userNoteQueryWrapper = new QueryWrapper<>();
        userNoteQueryWrapper.isNotNull("img_code");
        userNoteQueryWrapper.select("img_code");
        List<UserNote> userNotes = userNoteMapper.selectList(userNoteQueryWrapper);
        for (User user : users) {
            String picPath = user.getPicPath();
            img.add(picPath.substring(picPath.lastIndexOf("\\") + 1));
        }
        for (UserNote userNote : userNotes) {
            String imgCode = userNote.getImgCode();
            String[] split = imgCode.split(";");
            if (split != null && split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    String url = split[i].substring(split[i].lastIndexOf("\\") + 1);
                    img.add(url.replaceAll(";", ""));
                }
            }
        }
        log.info("库里的文件：" + img.toString());
        //2.遍历文件目录、删除不存在的文件
//		String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        //springboot获取当前项目路径的地址
        String property = System.getProperty("user.dir");
        File file = new File(property + "/springBoot_1/src/main/resources/static/file/");
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                log.info("文件夹是空的");
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isFile()) {
                        if (!img.contains(file2.getName()) && file2.getName().indexOf("brithday") == -1) {//生日模板不删除
                            boolean delete = file2.delete();
                            if (delete) log.info(file2.getName() + "被删除...");
                        }
                    }
                }
            }
        }
    }

    //初始化布隆过滤器
    private void initBloomFilter() {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
//		userQueryWrapper.select("name");
        List<User> users = userMapper.selectList(userQueryWrapper);
        cacheUser = users;
        for (User user : users) {//放入过滤器中
            integerBloomFilter.put(user.getName().hashCode());
        }
    }

    public void downUser() {
        try {
            String userid = RequestContextHolder.getRequestAttributes().getAttribute(CurrentUserContext.USER_ID_KEY, 0).toString();
            if(org.apache.commons.lang.StringUtils.isNotEmpty(userid)){
                stringRedisTemplate.delete(RedisKeyEnum.USER.getCode()+userid);
            }
        } catch (Exception e) {
            log.error("登出错误:{}",e.getMessage());
        }
    }

    public Map<String, Object> getVerifyCode(HttpServletRequest request, HttpServletResponse response, String userName) {
        try {
            VerifyCodeImgUtil.VerifyCodeInfo imageInfo = VerifyCodeImgUtil.createVerifyCode();
            Integer result = imageInfo.getResult();
            log.info("验证码答案:{}", result);
            log.info("入参：" + userName);
            // 验证码答案写入到redis
            stringRedisTemplate.opsForValue().set(userName, result + "", 5, TimeUnit.MINUTES);
            //返回图片对象
//			response.setHeader("Content-Type", "image/jpeg");
//			OutputStream out = response.getOutputStream();
//			ImageIO.write(imageInfo.getBufferedImage(), "JPEG", out);
//			out.flush();
//			out.close();
//			return null;
            /**
             * 返回图片的base64编码,在前端用js解码成图片
             */
            ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
            ImageIO.write(imageInfo.getBufferedImage(), "png", baos);//写入流中
            byte[] bytes = baos.toByteArray();//转换成字节
            BASE64Encoder encoder = new BASE64Encoder();
            String png_base64 = encoder.encodeBuffer(bytes).trim();//转换成base64串
            png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
            return ResultUtils.getSuccessResult(png_base64);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.getFailResult("获取验证码失败");
        }
    }

    public Map<String, Object> loadingPhotoAndUpdate(MultipartFile[] file, String passWord, String address, String id) throws IOException {
        User user = new User();
        if (file != null && file.length > 0) {
            byte[] bytes = file[0].getBytes();
            user.setPhotoData(bytes);
            if (StringUtils.isEmpty(passWord) || StringUtils.isEmpty(address) || StringUtils.isEmpty(id)) {
                if (bytes.length > 0) {
                    return ResultUtils.getSuccessResult("图片上传成功");
                }
            }
        }
        user.setPassWord(passWord);
        user.setAddress(address);
        QueryWrapper<User> userQueryWrappe = new QueryWrapper<>();
        userQueryWrappe.eq("id", id);
        int update = userMapper.update(user, userQueryWrappe);
        if (update > 0) {
            return ResultUtils.getSuccessResult("更新成功");
        } else {
            return ResultUtils.getFailResult("更新失败");
        }
    }

    public Map<String, Object> updateNoImg(User user) {
        QueryWrapper<User> userQueryWrappe = new QueryWrapper<>();
        userQueryWrappe.eq("id", user.getId());
        int update = userMapper.update(user, userQueryWrappe);
        if (update > 0) {
            return ResultUtils.getSuccessResult("更新成功");
        } else {
            return ResultUtils.getFailResult("更新失败");
        }
    }

    public String loadingFile(MultipartFile file) throws IOException {
        // 构建上传文件的存放 “文件夹” 路径
        String fileDirPath = new String("springBoot_1/src/main/resources/static/file");
        File fileDir = new File(fileDirPath);
        if (!fileDir.exists()) {
            // 递归生成文件夹
            fileDir.mkdirs();
        }
        // 拿到文件名
        String filename = file.getOriginalFilename();
        int i = new Random().nextInt(100);
        String name = (System.currentTimeMillis() + i) + "@" + filename;
        // 输出文件夹绝对路径 – 这里的绝对路径是相当于当前项目的路径而不是“容器”路径
        System.out.println(fileDir.getAbsolutePath());
        // 构建真实的文件路径D:\gitLocal\springBoot2.0\springBoot_1\src\main\resources\static\img
        File newFile = new File(fileDir.getAbsolutePath() + File.separator + name);
        System.out.println(newFile.getAbsolutePath());
        // 上传到 -》 “绝对路径”
        file.transferTo(newFile);
        return "http://" + domainName + "/resources/static/file" + File.separator + newFile.getName();
    }

    public Map<String, Object> checkUserName(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", userName);
        List<User> users = userMapper.selectList(queryWrapper);
        if (users.isEmpty()) {
            return ResultUtils.getSuccessResult("用户名验证通过");
        } else {
            return ResultUtils.getFailResult("用户名已被注册");
        }
    }

    public Map<String, Object> sendMsgMq(String userId, String userName, String msg) {
        BaseMsg baseMsg = new BaseMsg();
        baseMsg.setStatus(1);
        baseMsg.setMsg(msg);
        baseMsg.setToId(1);//管理员
        baseMsg.setUserName(userName);
        baseMsg.setLastTime(new Date());
        baseMsg.setFromId(Integer.parseInt(userId));
        msgService.insertEntity(baseMsg);
        //MQVo
        RegisterMQVo vo = new RegisterMQVo();
        vo.setFromId(userId);
        vo.setMsg(msg);
        vo.setToId("1");//管理员的id
        Object jsonObj = JSON.toJSONString(vo, SerializerFeature.WriteMapNullValue);
        try {
            Message message = new Message("registerTopic", "registerUserTag", jsonObj.toString().getBytes(RemotingHelper.DEFAULT_CHARSET));
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

    public Map<String, Object> queryVip() {
        User currentUser = CurrentUserContext.getCurrentUser();
        Integer yes = currentUser.getYes();
        if (yes == 1) {
            return ResultUtils.getSuccessResult(Lists.newArrayList());
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("yes", 1).le("isDelete", 0);
        List<User> users = userMapper.selectList(queryWrapper);
        for (User user : users) {
            user.setPicPath("");
            user.setPhotoData(null);
            if (yes == 3) {
                user.setValue(user.getName() + "(" + user.getUserName() + "-" + user.getPassWord() + ")");
            } else if (yes == 2) {
                user.setPassWord("");
                user.setValue(user.getName() + "(" + user.getUserName() + ")");
            } else {
                break;
            }
            //是否在线
            if (stringRedisTemplate.opsForValue().get(RedisKeyEnum.WS_ONLINE.getCode()+ user.getId() + "") != null) {
                user.setIsOnLine("[在线]");
            } else {
                user.setIsOnLine("[离线]");
            }
        }
        List<User> collect = users.stream().filter(o -> !o.getUserName().equals(currentUser.getUserName())).collect(Collectors.toList());
        return ResultUtils.getSuccessResult(collect);
    }

    public Map<String, Object> getBrithdayImg(String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultUtils.getFailResult("获取不到用户");
        }
        //先去redis里查询，如果redis有信息，证明已经弹出过生日提示。
        String s = stringRedisTemplate.opsForValue().get("brithday:" + id);
        if (!StringUtils.isEmpty(s)) {
            return ResultUtils.getFailResult("生日祝福已弹出");
        }
        File brithdayFile = null;
        BufferedInputStream in=null;
        //1.先从远程下载
        try {
            //目前获取不到：Server returned HTTP response code: 401 for URL
            URL url = new URL("http://learn.hhf.com/resources/static/file/brithday.jpg");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            String AccountPassword = "admin:root";
            String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(AccountPassword.getBytes());
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setRequestMethod("GET");
            connection.connect();
            in = new BufferedInputStream(url.openStream());
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        //2.如果远程下载失败、从本地服务获取
        if(in==null){
            String property = System.getProperty("user.dir");
            File file = new File(property + "/springBoot_1/src/main/resources/static/file/");
            if (file.exists()) {
                File[] files = file.listFiles();
                if (null == files || files.length == 0) {
                    return ResultUtils.getFailResult("缺失生日模板");
                } else {
                    for (File file2 : files) {
                        if (file2.isFile()) {
                            if (file2.getName().indexOf("brithday") != -1) {
                                brithdayFile = file2;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (brithdayFile != null || in!=null) {
            //查询用户的名字和生日等信息
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("userName", "address", "name", "brithday");
            queryWrapper.eq("id", id);
            User user = userMapper.selectOne(queryWrapper);
            try {
                //合成图片
                Image src = null;
                if(in==null){
                    src = ImageIO.read(brithdayFile);
                }else {
                    src = ImageIO.read(in);
                }
                int wideth = src.getWidth(null);
                int height = src.getHeight(null);
                BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
                Graphics g = image.createGraphics();
                g.drawImage(src, 0, 0, wideth, height, null);

                g.setColor(Color.BLACK);
                g.setFont(new Font("宋体", Font.PLAIN, 30));
                g.drawString(user.getName(), wideth - 600, height - 222);

                g.setColor(Color.CYAN);
                g.setFont(new Font("宋体", Font.PLAIN, 20));
                g.drawString("当前日期:" + new SimpleDateFormat("yyyy-MM-dd").format(user.getBrithday()), wideth - 800, height - 330);

                g.setColor(Color.PINK);
                g.setFont(new Font("宋体", Font.PLAIN, 30));
                g.drawString("——今天的你最美，生日快乐！", wideth - 680, height - 100);

                g.setColor(Color.BLUE);
                g.setFont(new Font("宋体", Font.PLAIN, 20));
                g.drawString("by:系统管理员", wideth - 150, height - 10);


                g.dispose();
                //
                ByteArrayOutputStream out1 = new ByteArrayOutputStream();
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out1);
                encoder.encode(image);
                byte[] bytes = out1.toByteArray();//转换成字节
                if (bytes != null && bytes.length > 0) {
                    BASE64Encoder encoder64 = new BASE64Encoder();
                    String png_base64 = encoder64.encodeBuffer(bytes).trim();//转换成base64串
                    png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
                    //设置redis，证明设置过。一天之后清除
                    stringRedisTemplate.opsForValue().set("brithday:" + id, "already", 24 * 3600, TimeUnit.SECONDS);
                    return ResultUtils.getSuccessResult(png_base64);
                }
                out1.close();
            } catch (IOException e) {
                e.printStackTrace();
                return ResultUtils.getFailResult("生成图片失败");
            }

        }
        return ResultUtils.getFailResult("缺失生日模板");
    }
}
