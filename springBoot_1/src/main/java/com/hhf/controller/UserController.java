package com.hhf.controller;

import java.util.Map;

import com.hhf.entity.ProductProManage;
import com.hhf.entity.ProductProManageExample;
import com.hhf.entity.User;
import com.hhf.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

	@RequestMapping(value="/insertDataByVue",method = RequestMethod.POST)
	public Map<String,Object> insertDataByVue(@RequestBody User user){
		return ResultUtils.getSuccessResult(userService.insertDataByVue(user));
	}

	@RequestMapping(value="/updateDataByVue",method = RequestMethod.POST)
	public Map<String,Object> updateDataByVue(@RequestBody User user){
		return ResultUtils.getSuccessResult(userService.updateDataByVue(user));
	}

	@RequestMapping("/queryByVue")
	public Map<String,Object> queryByVue(String userName,String passWord){
		return ResultUtils.getSuccessResult(userService.queryByVue(userName,passWord));
	}

	@RequestMapping("/deleteByVue")
	public Map<String,Object> deleteByVue(Long id){
		return ResultUtils.getSuccessResult(userService.deleteByVue(id));
	}

	// mybatis - generato 插件
	@Autowired
	private ProductService productService;

	@RequestMapping(value = "/saveProduct",method = RequestMethod.POST)
	public Map<String,Object> saveProduct(@RequestBody ProductProManage productProManage){
		productService.saveEntity(productProManage);
		return ResultUtils.getSuccessResult("保存成功");
	}

	@RequestMapping(value = "/queryProduct",method = RequestMethod.GET)
	public Map<String,Object> queryProduct(ProductProManage productProManage){
		ProductProManageExample example=new ProductProManageExample();
		ProductProManageExample.Criteria criteria = example.createCriteria();
		if(!StringUtils.isEmpty(productProManage.getProductName())){
			criteria.andProductNameEqualTo(productProManage.getProductName());
		}
		return ResultUtils.getSuccessResult(productService.selectByExample(example));
	}

}
