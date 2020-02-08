package com.hhf.controller;

import java.util.HashMap;
import java.util.Map;

import com.hhf.dubbo.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 启动方式2:单独写app
 * @author Administrator
 * 
 */

@EnableAutoConfiguration
@RestController
@RequestMapping("/my")
public class MyController {

	@Autowired
	private DubboService dubboService;
	
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

// nacos配置中心
	@Value("${myNacosValue}")
	private String value;
	@GetMapping("nacos/config")
	public String getConfig(){
		return this.value;
	}
//	ribbon客户端调用
	@GetMapping("nacos/ribbon")
	public  Map<String, Object> ribbon(Integer yes){
		return dubboService.ribbon(yes);
	}

}
