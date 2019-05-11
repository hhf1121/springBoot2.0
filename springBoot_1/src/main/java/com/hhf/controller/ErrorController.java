package com.hhf.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局异常
 * @author Administrator
 *
 */

@RestController
public class ErrorController {

	@RequestMapping("/getErrorInfo")
	public String getErrorInfo(Integer i) {
		int j=1/i;
		return "success:"+j;
	}
	
	
}
