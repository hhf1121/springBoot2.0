package com.hhf.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * springboot2.0
 * @author Administrator
 *
 */
@EnableAutoConfiguration //开启自动装配
@RestController			//此controller下面所有方法返回json（@ResponseBody）：springCloud微服务框架中 返回json、      http+json
public class IndexController {

	@RequestMapping("/index")
	public String index() {
		return "hello,SpringBoot2.0";
	}
	
	//SpringBoot集成web容器。不使用tomcat，直接使用main函数、启动。只能启动本controller
	public static void main(String[] args) {
		SpringApplication.run(IndexController.class,args);
	}
}
