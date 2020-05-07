package com.hhf.controller;

import com.hhf.dubbo.DubboService;
import com.hhf.entity.ProductProManage;
import com.hhf.entity.ProductProManageExample;
import com.hhf.entity.User;
import com.hhf.rocketMQ.MQProducer;
import com.hhf.service.AsynService;
import com.hhf.service.JDBCService;
import com.hhf.service.ProductService;
import com.hhf.service.UserService;
import com.hhf.utils.ResultUtils;
import com.hhf.utils.VerifyCodeImgUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
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
    private DubboService dubboService;//dubboService

    @Autowired
    private AsynService asynService;//异步service

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

    @RequestMapping("dubbo/dubboData")
    public Map<String, Object> getDataByDubbo(Integer yes) {
        return dubboService.dubboData(yes);
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

    //VUE-对应接口
    @RequestMapping(value = "vue/insertDataByVue", method = RequestMethod.POST)
    public Map<String, Object> insertDataByVue(@RequestBody User user) {
        return ResultUtils.getSuccessResult(userService.insertDataByVue(user));
    }

    //VUE-对应接口
    @RequestMapping(value = "vue/updateDataByVue", method = RequestMethod.POST)
    public Map<String, Object> updateDataByVue(@RequestBody User user) {
        return ResultUtils.getSuccessResult(userService.updateDataByVue(user));
    }


    //VUE-对应接口
    @RequestMapping("vue/queryByVue")
    public Map<String, Object> queryByVue(String userName, String passWord,String verifyCode, HttpServletResponse httpServletResponse) {
        User user= null;
        try{
            user = userService.queryByVue(userName, passWord, verifyCode, httpServletResponse);
        }catch (RuntimeException e){
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

    //下线用户
    @GetMapping("/downUser")
    public void downUser(HttpServletRequest httpServletRequest,HttpServletResponse response) throws IOException {
         userService.downUser(httpServletRequest,response);
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

}
