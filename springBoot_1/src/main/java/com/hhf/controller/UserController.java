package com.hhf.controller;

import com.alibaba.fastjson.JSONArray;
import com.hhf.entity.ProductProManage;
import com.hhf.entity.ProductProManageExample;
import com.hhf.entity.User;
import com.hhf.rocketMQ.MQProducer;
import com.hhf.service.AsynService;
import com.hhf.service.JDBCService;
import com.hhf.service.ProductService;
import com.hhf.service.UserService;
import com.hhf.utils.JwtUtils;
import com.hhf.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.*;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

//import com.hhf.dubbo.DubboService;

/**
 * userController
 * 集成mybatis、actuator 监控中心、PageHelper
 *
 * @author Administrator
 */


@Slf4j
@RestController
@RequestMapping("/springBoot")
public class UserController {

    @Autowired
    private UserService userService;//userService

    @Autowired
    private JDBCService JDBCService;//jdbcService

    @Autowired
    private ProductService productService;//mybatis-generator生成example

    @Autowired
    private MQProducer mqProducer;//mqService


    @Autowired
    private AsynService asynService;//异步service

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //测试mq
    @RequestMapping("mq/producer")
    public Map<String, Object> callback(String message) {
        try {
            return mqProducer.send("hhfTopic", "mytag", message);
        } catch (UnsupportedEncodingException e) {
            return ResultUtils.getFailResult(e.getMessage());
        } catch (InterruptedException e) {
            return ResultUtils.getFailResult(e.getMessage());
        } catch (RemotingException e) {
            return ResultUtils.getFailResult(e.getMessage());
        } catch (MQClientException e) {
            return ResultUtils.getFailResult(e.getMessage());
        } catch (MQBrokerException e) {
            return ResultUtils.getFailResult(e.getMessage());
        }
    }

    @RequestMapping("asyn/addObject")
    public String addObject() throws InterruptedException, ExecutionException {
        log.info("Controller调用Service：addObject");
        String addObjet = asynService.addObjet();
        log.info("service层执行结果:" + addObjet);
        if (StringUtils.isEmpty(addObjet)) {
            return "asynTask：asynService.addObjet()还没有返回值..";
        }
        return "Success";
    }

    //分页
    @RequestMapping("user/query")
    public Map<String, Object> query(String name, int pageNum, int pageSize) {
        return ResultUtils.getSuccessResult(userService.query(name,pageNum,pageSize));
    }


    @PostMapping("user/queryVIP")
    public Map<String, Object> queryUserByType(@RequestBody User user) {
        return ResultUtils.getSuccessResult(userService.queryVIP(user));
    }

    @RequestMapping("user/insertUser")
    public Map<String, Object> insertUser(String userName, String passWord) {
        return ResultUtils.getSuccessResult(userService.insertUser(userName, passWord));
    }

    //分页
    @PostMapping("user/queryPage")
    public Map<String, Object> queryPage(@RequestBody User user) {
        return ResultUtils.getSuccessResult(userService.queryPage(user));
    }

    //文件上传接口
    @RequestMapping(value = "vue/loadingFile", method = RequestMethod.POST)
    public Map<String, Object> loadingUserImg(@RequestParam("file") MultipartFile file) {
        String result="";
        try{
            result = userService.loadingFile(file);
        }catch (Exception e ){
            return ResultUtils.getFailResult("上传文件异常！");
        }
        return ResultUtils.getSuccessResult(result);
    }


    //VUE-对应接口(新增用户)
    @RequestMapping(value = "vue/insertDataByVue", method = RequestMethod.POST)
    public Map<String, Object> insertDataByVue(@RequestBody User user) {
        return ResultUtils.getSuccessResult(userService.insertDataByVue(user));
    }


    //VUE-对应接口
    @RequestMapping(value = "vue/updateDataByVue", method = RequestMethod.POST)
    public Map<String, Object> updateDataByVue(@RequestBody User user) {
        return ResultUtils.getSuccessResult(userService.updateDataByVue(user));
    }


    //VUE-对应接口（登录）
    @RequestMapping("vue/queryByVue")
    public Map<String, Object> queryByVue(String userName, String passWord,String verifyCode, HttpServletResponse httpServletResponse,HttpServletRequest request) {
        User user= null;
        try{
            //查询用户
            user = userService.queryByVue(userName, passWord, verifyCode, httpServletResponse,request);
            //是否生日
            if(user!=null&&user.getBrithday()==null){
                user.setIsBrithday("isError");
            }else {
                java.util.Date date = user.getBrithday();
                Instant instant = date.toInstant();
                ZoneId zone = ZoneId.systemDefault();
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
                LocalDate brithdayDate = localDateTime.toLocalDate();
                if(user!=null&&brithdayDate.compareTo(LocalDate.now())==0){
                    user.setIsBrithday("isBrithday");
                }
                if(user!=null&&brithdayDate.compareTo(LocalDate.now())!=0){
                    user.setIsBrithday("isNoBrithday");
                }
            }
        }catch (RuntimeException e){
            return ResultUtils.getFailResult(e.getMessage());
        } catch (IOException e) {
            return ResultUtils.getFailResult(e.getMessage());
        }
        return ResultUtils.getSuccessResult(user);
    }

    @RequestMapping("vue/deleteByVue")
    public Map<String, Object> deleteByVue(Long id) {
        return ResultUtils.getSuccessResult(userService.deleteByVue(id));
    }


    // mybatis - generator 插件
    @RequestMapping(value = "config/generator/saveProduct", method = RequestMethod.POST)
    public Map<String, Object> saveProduct(@RequestBody ProductProManage productProManage) {
        productService.saveEntity(productProManage);
        return ResultUtils.getSuccessResult("保存成功");
    }

    @RequestMapping(value = "config/generator/queryProduct", method = RequestMethod.GET)
    public Map<String, Object> queryProduct(ProductProManage productProManage) {
        ProductProManageExample example = new ProductProManageExample();
        ProductProManageExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(productProManage.getProductName())) {
            criteria.andProductNameEqualTo(productProManage.getProductName());
        }
        return ResultUtils.getSuccessResult(productService.selectByExample(example));
    }

    //jdbc操作
    @RequestMapping("jdbc/insertUser")
    public Map<String, Object> insertUserByJDBC(String username, String password) {
        User user = new User();
        user.setUserName(username);
        user.setPassWord(password);
        return ResultUtils.getSuccessResult(JDBCService.createUserByJDBC(user));
    }

    //全局异常
    @RequestMapping("error/getErrorInfo")
    public String getErrorInfo(Integer i) {
        int j = 1 / i;
        return "success:" + j;
    }

    //当前用户
    @GetMapping("/getCurrentUser")
    public User getCurrentUser(String id){
	    if(StringUtils.isEmpty(id))
	        return null;
	    return userService.getCurrentUser(Long.valueOf(id));
    }


    //当前用户
    @GetMapping("/getCurrentUserStr")
    public String getCurrentUserStr(String id){
        if(StringUtils.isEmpty(id))
            return null;
        return userService.getCurrentUserStr(Long.valueOf(id));
    }


    //校验当前用户是否已登录
    @GetMapping("/currentIsLogin")
    public Map<String,Object> currentIsLogin(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String token = "";
        if(cookies!=null&&cookies.length>0){
            for (Cookie cookie : cookies) {
                if (org.apache.commons.lang3.StringUtils.equals(cookie.getName(),"myToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        String s = stringRedisTemplate.opsForValue().get(token);
        if(StringUtils.isEmpty(s)){
            return ResultUtils.getFailResult("用户未登录");
        }
        User user = JSONArray.parseObject(s, User.class);
        user.setToken(JwtUtils.generateById(user.getId()));

        return ResultUtils.getSuccessResult(user);
    }

    //下线用户
    @GetMapping("/downUser")
    public void downUser(){
         userService.downUser();
    }

    /**
     *
     * @param request
     * @param response
     * @param userName
     * @return
     * 获取验证码
     */
    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getVerifyCode(HttpServletRequest request,HttpServletResponse response,String userName){
        return userService.getVerifyCode(request,response,userName);
    }


    /**
     *
     * @param files
     * @param passWord
     * @param address
     * @param id
     * @return
     * 修改用户信息（包含头像）
     */
    @RequestMapping(value = "/loadingPhoto",method = RequestMethod.POST)
    public Map<String,Object> loadingPhoto(@RequestParam("file") MultipartFile[] files,
                                           @RequestParam(value = "passWord",required = false) String passWord,
                                           @RequestParam(value="address",required = false) String address,
                                           @RequestParam(value="id",required = false) String id) {
        try {
            return userService.loadingPhotoAndUpdate(files,passWord,address,id);
        } catch (Exception e) {
            return ResultUtils.getFailResult("更新异常");
        }
    }

    /**
     * 更新用户（忽略头像）
     * @param user
     * @return
     */
    @RequestMapping(value = "/updateNoImg",method = RequestMethod.POST)
    public Map<String,Object> updateNoImg(@RequestBody User user) {
        try {
            return userService.updateNoImg(user);
        } catch (Exception e) {
            return ResultUtils.getFailResult("更新异常");
        }
    }

    @GetMapping(value = "/checkUserName")
    public Map<String,Object> checkUserName(String userName) {
        try {
            return userService.checkUserName(userName);
        } catch (Exception e) {
            return ResultUtils.getFailResult("更新异常");
        }
    }

    //注册用户
    @RequestMapping(value = "registerUser", method = RequestMethod.POST)
    public Map<String, Object> registerUser(@RequestBody User user) {
        return ResultUtils.getSuccessResult(userService.insertDataByVue(user));
    }

    //注册-给管理员发信息
    @RequestMapping(value = "sendAdmin", method = RequestMethod.GET)
    public Map<String, Object> sendAdmin(String userId,String userName,String msg) {
         return userService.sendMsgMq(userId,userName,msg);
    }


    //注册-给管理员发信息
    @GetMapping(value = "queryVip")
    public Map<String, Object> queryVip() {
        return userService.queryVip();
    }

    //获取生日祝福图片
    @GetMapping(value = "getBrithdayImg")
    public Map<String, Object> getBrithdayImg(String id) {
        return userService.getBrithdayImg(id);
    }


}
