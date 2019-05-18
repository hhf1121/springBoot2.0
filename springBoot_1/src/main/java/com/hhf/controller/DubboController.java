package com.hhf.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hhf.dubbo.DubboService;

@RestController
public class DubboController {


	@Autowired
	private DubboService dubboService;
	
	@RequestMapping("dubboIndex")
	public Map<String,Object> getDataByDubbo(Integer yes){
		return dubboService.dubboData(yes);
	}
	
}
