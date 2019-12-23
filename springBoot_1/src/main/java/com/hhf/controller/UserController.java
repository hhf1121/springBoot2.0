package com.hhf.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.hhf.dubbo.DubboService;
import com.hhf.dubbo.DubboService;
import com.hhf.entity.ProductProManage;
import com.hhf.entity.ProductProManageExample;
import com.hhf.entity.User;
import com.hhf.rocketMQ.MQProducer;
import com.hhf.service.AsynService;
import com.hhf.service.JDBCService;
import com.hhf.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.hhf.service.UserService;
import com.hhf.utils.ResultUtils;

/**
 * userController
 * 集成mybatis、actuator 监控中心、PageHelper
 * @author Administrator
 *
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
	public Map<String,Object> callback(String message) {
		try {
			return mqProducer.send("myTopic","mytag",message);
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
	public Map<String,Object> getDataByDubbo(Integer yes){
		return dubboService.dubboData(yes);
	}

	@RequestMapping("asyn/addObject")
	public String addObject() throws InterruptedException, ExecutionException {
		log.info("Controller调用Service：addObject");
		String addObjet = asynService.addObjet();
		log.info("service层执行结果:"+addObjet);
		if(StringUtils.isEmpty(addObjet)) {
			return "asynTask：asynService.addObjet()还没有返回值..";
		}
		return "Success";
	}

	//分页
	@RequestMapping("user/query")
	public Map<String,Object> query(String name,int pageNum,int pageSize){
		return null;//ResultUtils.getSuccessResult(userService.query(name,pageNum,pageSize));
	}


	@PostMapping("user/queryVIP")
	public Map<String,Object> queryUserByType(@RequestBody User user){
		return ResultUtils.getSuccessResult(userService.queryVIP(user));
	}
	
	@RequestMapping("user/insertUser")
	public Map<String,Object> insertUser(String userName,String passWord){
		return ResultUtils.getSuccessResult(userService.insertUser(userName, passWord));
	}

	//分页
	@PostMapping("user/queryPage")
	public Map<String,Object> queryPage(@RequestBody User user){
		return ResultUtils.getSuccessResult(userService.queryPage(user));
	}

	//VUE-对应接口
	@RequestMapping(value="vue/insertDataByVue",method = RequestMethod.POST)
	public Map<String,Object> insertDataByVue(@RequestBody User user){
		return ResultUtils.getSuccessResult(userService.insertDataByVue(user));
	}

	//VUE-对应接口
	@RequestMapping(value="vue/updateDataByVue",method = RequestMethod.POST)
	public Map<String,Object> updateDataByVue(@RequestBody User user){
		return ResultUtils.getSuccessResult(userService.updateDataByVue(user));
	}

	//VUE-对应接口
	@RequestMapping("vue/queryByVue")
	public Map<String,Object> queryByVue(String userName,String passWord){
		return ResultUtils.getSuccessResult(userService.queryByVue(userName,passWord));
	}

	@RequestMapping("vue/deleteByVue")
	public Map<String,Object> deleteByVue(Long id){
		return ResultUtils.getSuccessResult(userService.deleteByVue(id));
	}


	// mybatis - generator 插件
	@RequestMapping(value = "config/generator/saveProduct",method = RequestMethod.POST)
	public Map<String,Object> saveProduct(@RequestBody ProductProManage productProManage){
		productService.saveEntity(productProManage);
		return ResultUtils.getSuccessResult("保存成功");
	}

	@RequestMapping(value = "config/generator/queryProduct",method = RequestMethod.GET)
	public Map<String,Object> queryProduct(ProductProManage productProManage){
		ProductProManageExample example=new ProductProManageExample();
		ProductProManageExample.Criteria criteria = example.createCriteria();
		if(!StringUtils.isEmpty(productProManage.getProductName())){
			criteria.andProductNameEqualTo(productProManage.getProductName());
		}
		return ResultUtils.getSuccessResult(productService.selectByExample(example));
	}

	//jdbc操作
	@RequestMapping("jdbc/insertUser")
	public Map<String,Object> insertUserByJDBC(String username,String password){
		User user=new User();
		user.setUserName(username);
		user.setPassWord(password);
		return ResultUtils.getSuccessResult(JDBCService.createUserByJDBC(user));
	}

	//全局异常
	@RequestMapping("error/getErrorInfo")
	public String getErrorInfo(Integer i) {
		int j=1/i;
		return "success:"+j;
	}

}
