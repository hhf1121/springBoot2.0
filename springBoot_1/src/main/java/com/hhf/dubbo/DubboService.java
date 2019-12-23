package com.hhf.dubbo;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DubboService{

	@Reference(version="1.0.0")
	private IDubboService dubboService;


	public Map<String,Object> dubboData(Integer yes) {
		log.info("----------dubboData-----------");
		return  dubboService.getRPCData(yes);
	}
}
