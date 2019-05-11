package com.hhf.utils;

import java.util.HashMap;
import java.util.Map;

public class ResultUtils {

	public static Map<String,Object> getSuccessResult(Object object){
		Map<String,Object> map =new HashMap<>();
		map.put("data",object);
		map.put("success",true);
		return map;
	} 
	
	
	public static Map<String,Object> getFailResult(Object object){
		Map<String,Object> map =new HashMap<>();
		map.put("error",object);
		map.put("success",false);
		return map;
	} 
	
}
