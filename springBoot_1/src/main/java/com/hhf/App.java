package com.hhf;

import com.hhf.interceptor.UserLoginInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
//@MapperScan(basePackages= {"com.hhf.mapper"})
//@EnableDiscoveryClient
public class App implements WebMvcConfigurer {

	@Autowired
	private UserLoginInterceptor userLoginInterceptor;

	public static void main(String[] args) {
		SpringApplication.run(App.class,args);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry){
		//添加对用户是否登录的拦截器，并添加过滤项、排除项
//		registry.addInterceptor(userLoginInterceptor).addPathPatterns("/**");
//				.excludePathPatterns("/css/**","/js/**","/images/**")//排除样式、脚本、图片等资源文件
//				.excludePathPatterns("/wechatplatformuser/loginRBAC.html")//排除登录页面
//				.excludePathPatterns("/wechatplatformuser/defaultKaptcha")//排除验证码
//				.excludePathPatterns("/wechatplatformuser/loginRBAC");//排除用户点击登录按钮
	}

	//初始化redisson
//	@Bean
//	public Redisson redisson(){
//		Config config=new Config();
//		config.useSingleServer().setAddress("redis://www.hhf.com:6379").setPassword("feixiang").setDatabase(0);
//		return (Redisson) Redisson.create(config);
//	}


	/**
	 * 请求参数，转换器:字符串->时间
	 * @return
	 */
	@Bean
	public Converter<String, Date> addNewConvert() {
		return new Converter<String, Date>() {
			@Override
			public Date convert(String source) {
				String replace = source.replace("T", " ");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
				Date date = null;
				try {
					 date =sdf.parse(replace);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return date;
			}
		};
	}
	
}
