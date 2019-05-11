package com.hhf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Springboot全局启动app
 * @author Administrator
 *
 */
//第二种启动方式：
//@EnableAutoConfiguration
//@ComponentScan("com.hhf.controller")//扫描此包下的所有controller、并启动
//第三种启动方式：(扫描的类，在同包或之下。)
@SpringBootApplication
//@EnableAsync//开启异步调用
@MapperScan(basePackages= {"com.hhf.mapper"})
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class,args);
	}
	
}
