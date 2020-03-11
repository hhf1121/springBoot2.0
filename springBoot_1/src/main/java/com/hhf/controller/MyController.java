package com.hhf.controller;

import java.util.HashMap;
import java.util.Map;

import com.hhf.api.providerApi;
import com.hhf.dubbo.DubboService;
import com.hhf.service.IMyService;
import org.hibernate.annotations.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 启动方式2:单独写app
 * @author Administrator
 * 
 */

@EnableAutoConfiguration
@RestController
@RequestMapping("/my")
public class MyController {

	//使用dubbo调用
	@Autowired
	private DubboService dubboService;

	//使用feign客户端调用
	@Autowired
	private providerApi providerapi;

	//使用template调用
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IMyService myService;

	//获取application配置文件里面的值：初始化的时候
	@Value("${disconf.name}")
	private String name;

	@RequestMapping("/myIndex")
	public Map<String,Object> getMyIndex(){
		Map<String,Object> map=new HashMap<>();
		map.put("data","访问成功");
		map.put("success",true);
		return map;
	}
	
	@RequestMapping("/getName")
	public String getName(){
		return name;
	}

// nacos配置中心
	@Value("${myNacosValue}")
	private String value;
	@GetMapping("nacos/config")
	public String getConfig(){
		return this.value;
	}

//	template调用:加了@LoadBalanced之后，负载均衡、从注册中心服务名称上找
    @GetMapping("nacos/template")
    public  Map<String, Object> template(Integer yes){
        ResponseEntity<Map> responseEntity= restTemplate.getForEntity("http://provider-service/getDateBySentinel?yes="+yes, Map.class);
        Map<String, Object> body = responseEntity.getBody();
    return body;
}

//	ribbon客户端调用
	@GetMapping("nacos/ribbon")
	public  Map<String, Object> ribbon(Integer yes){
		return dubboService.ribbon(yes);
	}
//  feign调用
	@GetMapping("nacos/feign")
	public  Map<String, Object> feign(Integer yes){
		return providerapi.getDataByFeign(yes);
	}

	@GetMapping("isTranaction")
    public Map<String,Object> isTranaction(Long id){
	    return myService.isTranaction(id);
    }

}
