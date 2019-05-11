package com.hhf.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局捕获异常工具类
 * @author Administrator
 *
 */

//@ControllerAdvice(basePackages="com.hhf.controller")//aop哪个包下的。  (此处注释掉全局异常，以便开发排错)
public class GlobalExceptionController {

	
	@ExceptionHandler(RuntimeException.class)//捕获此异常。
	@ResponseBody
	public Map<String,Object> errorResult(){
		//正常项目中，一般是会把异常记录到日志中，而不是返回错误信息、
		Map<String,Object> resultMap=new HashMap<String,Object>();
		resultMap.put("errorCode", "500");
		resultMap.put("errorMsg", "RuntimeException");
		return resultMap;
	}
	
}
