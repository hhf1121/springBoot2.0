package com.hhf.dubbo;

import java.util.Map;

import com.hhf.api.IDubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class DubboService{

	@Autowired
	private RestTemplate restTemplate;

	@Reference(version="1.0.0")
	private IDubboService dubboService;


	public Map<String,Object> dubboData(Integer yes) {
		log.info("----------dubboData-----------");
		return  dubboService.getRPCData(yes);
	}

	public Map<String, Object> ribbon(Integer yes){
		ResponseEntity<Map> responseEntity= restTemplate.getForEntity("http://provider-service/getDate?yes="+yes, Map.class);
		Map<String, Object> result = responseEntity.getBody();
		return result;
	}

}
