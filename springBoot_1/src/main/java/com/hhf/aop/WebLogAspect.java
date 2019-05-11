package com.hhf.aop;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * aop：打印log工具类  
 * @author Administrator
 *	更深层次：分布式日志收集（待续...）
 */

@Aspect
@Component
public class WebLogAspect {

	private static final Logger logger=LoggerFactory.getLogger(WebLogAspect.class);
	
	@Pointcut("execution(public * com.hhf.controller.*.*(..))")
	public void webLog() {
		
	}
	
	@Before("webLog()")
	public void doBefore(JoinPoint joinPoint) {
		//接收到请求，记录请求内容
		ServletRequestAttributes attributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		//记录内容：一般是记录在mongdb中（nosql类型）
		logger.info("URL:"+request.getRequestURL());
		logger.info("HTTP_METHOD:"+request.getMethod());
		logger.info("IP:"+request.getRemoteAddr());
		Enumeration<String> parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String name=parameterNames.nextElement();
			logger.info("name:{},value:{}",name,request.getParameter(name));
		}
	}
	
	@AfterReturning(returning="ret",pointcut="webLog()")
	public void doAfterReturning(Object ret) {
		logger.info("RESPINSE:"+ret);
	}
	
}
