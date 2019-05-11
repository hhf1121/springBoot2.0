package com.hhf.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 启动方式2:单独写app
 * @author Administrator
 * 
 */

@EnableAutoConfiguration
@RestController
public class MyController {
	
	//获取application配置文件里面的值：初始化的时候
	@Value("${disconf.name}")
	private String name;

	@RequestMapping("/myIndex")
	public Map<String,Object> getMyIndex(){
		Map<String,Object> map=new HashMap<>();
		map.put("data","访问成功");
		map.put("success",true);
		return map;
	}
	
	@RequestMapping("/getName")
	public String getName(){
		return name;
	}
}
