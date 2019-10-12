package com.hhf.aop;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hhf.entity.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * aop：记录log工具类
 * @author Administrator
 *	更深层次：分布式日志收集（待续...）
 */

@Aspect
@Component
public class WebLogAspect {

//	@Autowired
//	private MongoTemplate mongoTemplate;

	private Log log=null;

	private static final Logger logger=LoggerFactory.getLogger(WebLogAspect.class);
	
	@Pointcut("execution(public * com.hhf.controller.*.*(..))")
	public void webLog() {

	}
	
	@Before("webLog()")
	public void doBefore(JoinPoint joinPoint) {
		log=new Log();
		//接收到请求，记录请求内容
		ServletRequestAttributes attributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		//记录内容：一般是记录在mongdb中（nosql类型）
		log.setURL(request.getRequestURL().toString());
		log.setIP(request.getRemoteAddr());
		log.setHTTP_METHOD(request.getMethod());
		logger.info("URL:"+request.getRequestURL());
		logger.info("HTTP_METHOD:"+request.getMethod());
		logger.info("IP:"+request.getRemoteAddr());
		Enumeration<String> parameterNames = request.getParameterNames();
		Map<String,String> params=new HashMap<String,String>();
		while(parameterNames.hasMoreElements()) {
			String name=parameterNames.nextElement();
			params.put("name",name);
			params.put("value",request.getParameter(name));
			logger.info("name:{},value:{}",name,request.getParameter(name));
		}
		log.setParams(params);
	}
	
	@AfterReturning(returning="ret",pointcut="webLog()")
	public void doAfterReturning(Object ret) {
		logger.info("RESPINSE:"+ret);
		log.setRESPINSE(ret.toString());
		log.setDate(new Date());
//		mongoTemplate.insert(log);
	}
	
}
