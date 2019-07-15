package com.hhf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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
@EntityScan("com.hhf.entity")//支持jpa
@EnableJpaRepositories(basePackages={"com.hhf.mapper"})//支持jpa：1.jpa扫描接口
@MapperScan(basePackages= {"com.hhf.mapper"})
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class,args);
	}
	
}
