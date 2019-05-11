package com.hhf.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hhf.service.AsynService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/asyn")
public class AsynController {

	@Autowired
	private AsynService asynService;
	
	@RequestMapping("/addObject")
	public String addObject() throws InterruptedException, ExecutionException {
		log.info("Controller调用Service：addObject");
		String addObjet = asynService.addObjet();
		log.info("service层执行结果:"+addObjet);
		if(StringUtils.isEmpty(addObjet)) {
			return "asynTask：asynService.addObjet()还没有返回值..";
		}
		return "Success";
	}
}
