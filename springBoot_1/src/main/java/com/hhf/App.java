package com.hhf;

import com.hhf.interceptor.UserLoginInterceptor;
import com.hhf.webSocket.WebSocketServer;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
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
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@EnableAsync//开启异步调用
@EntityScan("com.hhf.entity")//支持jpa
@EnableJpaRepositories(basePackages={"com.hhf.mapper"})//支持jpa：1.jpa扫描接口
//@MapperScan(basePackages= {"com.hhf.mapper"})
//@EnableDiscoveryClient
@EnableFeignClients //开启fegin调用
//@EnableTransactionManagement//开启本地事务，配合@Transactional
@EnableScheduling
public class App {

	public static void main(String[] args) {
		/**
		 * Springboot整合Elasticsearch 在项目启动前设置一下的属性，防止报错
		 * 解决netty冲突后初始化client时还会抛出异常
		 * java.lang.IllegalStateException: availableProcessors is already set to [4], rejecting [4]
		 */
		System.setProperty("es.set.netty.runtime.available.processors", "false");
		ConfigurableApplicationContext run = SpringApplication.run(App.class, args);
		//手动注入bean
		WebSocketServer.setStringRedisTemplate((StringRedisTemplate) run.getBean("stringRedisTemplate"));
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
