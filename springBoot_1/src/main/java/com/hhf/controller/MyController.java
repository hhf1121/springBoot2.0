package com.hhf.controller;

import com.google.common.collect.Lists;
import com.hhf.dubbo.DubboService;
import com.hhf.feignClient.FeignHttpServer;
import com.hhf.feignClient.PortalAgencyCenterDto;
import com.hhf.service.IMyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import com.hhf.api.providerApi;

/**
 * 启动方式2:单独写app
 * @author Administrator
 * 
 */

@EnableAutoConfiguration
@RestController
@RequestMapping("/my")
@RefreshScope//获取nacos的新配置
@Slf4j
public class MyController {

	//使用dubbo调用
	@Autowired
	private DubboService dubboService;

	//使用feign客户端调用
//	@Autowired
//	private providerApi providerapi;

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
//		return dubboService.ribbon(yes);
		return null;
	}
//  feign调用
	@GetMapping("nacos/feign")
	public  Map<String, Object> feign(Integer yes){
//		return providerapi.getDataByFeign(yes);
		return null;
	}

	@GetMapping("isTranaction")
    public Map<String,Object> isTranaction(Long id){
	    return myService.isTranaction(id);
    }

	@RequestMapping("dubbo/dubboData")
	public Map<String, Object> getDataByDubbo(Integer yes) {
        return dubboService.dubboData(yes);
//		return null;
	}

	@Autowired
	private FeignHttpServer feignHttpServer;

	@Autowired
	private StringRedisTemplate redisTemplate;


	@GetMapping("feign/url")
	public void getUrlType() {
		try {
			PortalAgencyCenterDto dto=new PortalAgencyCenterDto();
			dto.setAccessKey("eOzAt3041Sbb9n3EjcNPS76evt3H7p1s");
			dto.setAcceptanceType(1);
			dto.setSourceCode("UFE");
			dto.setSourceType("uce");
			dto.setSourceSign("WEIYIBIANMA"+redisTemplate.opsForValue().increment("HHF"));//待办唯一码,根据此字段更新状态
			dto.setAgentCodes(Lists.newArrayList("050069"));
			dto.setAgencyTitle("UFE待办标题"+redisTemplate.opsForValue().increment("HHF"));
			dto.setAgencyType("1");//大类
			dto.setAgencyCategory("测试接收时间");//小类
			dto.setCallbackUrl("http://192.168.202.53:8081/#/ChinaMap/about");
			dto.setAcceptanceTime(new Date());
			Map<String, Object> map = feignHttpServer.sendAgency(dto);
			log.info(map.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping("feign/nacos")
	public Map<String, Object> getUrlType(Integer yes) {
		return feignHttpServer.getDataByFeign(yes);
	}



}
