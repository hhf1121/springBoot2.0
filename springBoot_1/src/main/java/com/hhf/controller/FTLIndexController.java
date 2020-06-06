package com.hhf.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 整合freemarker视图层:返回页面,默认返回的是resource/templates下的*.ftl文件
 * @author Administrator
 *
 */

@Controller
public class FTLIndexController {

	@RequestMapping("/ftlIndex")
	public String ftlIndex(Map<String,Object> map,String name,Integer sex) {
		//此处存疑：map是形参的时候，正常运行。
//		Map<String,Object> map= new HashMap<String,Object>();
		map.put("name", name);
		map.put("sex", sex);
		map.put("age", 30);
		return "ftlIndex";
	}


	/**
	 * 返回html页面
	 * 引入：spring-boot-starter-thymeleaf
	 * @param map
	 * @param name
	 * @param sex
	 * @return
	 */
	@RequestMapping("/htmlIndex")
	public String htmlIndex(Map<String,Object> map,String name,Integer sex) {
//		Map<String,Object> map= new HashMap<String,Object>();
		map.put("name", name);
		map.put("sex", sex);
		map.put("age", 30);
		return "htmlIndex";
	}
	
}
