package com.hhf.controller;

import com.google.common.collect.Lists;
import com.hhf.dubbo.DubboService;
import com.hhf.feignClient.FeignHttpServer;
import com.hhf.feignClient.PortalAgencyCenterDto;
import com.hhf.service.IMyService;
import com.hhf.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

	@RequestMapping("/redis_lua")
	public Map<String,Object> RedisLua(){
		String script = "--返回的变量\n" +
				"local result = {}\n" +
				"--获取KEY\n" +
				"--开抢时间\n" +
				"local key1 = KEYS[1]\n" +
				"--过期时间\n" +
				"local key2 = KEYS[2]\n" +
				"--红包金额分配key\n" +
				"local key3 = KEYS[3]\n" +
				"--红包领取记录key\n" +
				"local key4 = KEYS[4]\n" +
				"--红包实体信息\n" +
				"--local key5 = KEYS[5]\n" +
				"\n" +
				"--获取value ,占时是uid\n" +
				"--local value1 = ARGV[1]\n" +
				"--1.校验是否过期\n" +
				"if( redis.call('ttl',key2) == -2)\n" +
				"then\n" +
				"result[1] = '1'\n" +
				"result[2] =   cjson.encode('红包已过期')\n" +
				"return result;\n" +
				"end\n" +
				"\n" +
				"--2.校验是否触发\n" +
				"local trigger_time = redis.call('ttl',key1)\n" +
				"if(trigger_time ~= -2)\n" +
				"then\n" +
				"result[1] = '2'\n" +
				"result[2] =  trigger_time\n" +
				"return result;\n" +
				"end\n" +
				"\n" +
				"--3.校验是否已经抢过\n" +
				"if (redis.call('hexists',key4,ARGV[1])  ~=0) \n" +
				"then\n" +
				"result[1] = '3'\n" +
				"result[2] =  cjson.encode('已抢到红包')\n" +
				"return result;\n" +
				"else\n" +
				"local re = redis.call('rpop',key3)\n" +
				"if re\n" +
				"then\n" +
				"redis.call('hset',key4,ARGV[1],1)\n" +
				"result[1] = '4'\n" +
				"result[2] =  re\n" +
				"return result;\n" +
				"else\n" +
				"result[1] = '5'\n" +
				"result[2] =  cjson.encode('来慢了')\n" +
				"return result;\n" +
				"end\n" +
				"end\n" +
				"\n" +
				" \n" +
				"\n" +
				"\n" +
				"\n" +
				"\n" +
				"\n" +
				"\n" +
				"\n";



		DefaultRedisScript<List> defaultRedisScript = new DefaultRedisScript<>();
		defaultRedisScript.setResultType(List.class);
		defaultRedisScript.setScriptText(script);

		List<String> keyList = new ArrayList<>();
		keyList.add("RED_ENVELOPE_LOCK_KEY");
		keyList.add("RED_ENVELOPE_TTL_KEY");
		keyList.add("RED_ENVELOPE_AMOUNT_KEY");
		keyList.add("RED_ENVELOPE_RECORD");


		List execute = redisTemplate.execute(defaultRedisScript, keyList, "123");
		execute.forEach(o->log.info(o.toString()));
		return ResultUtils.getSuccessResult("成功");
	}


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

	//手动获取以??为前缀的key
	@GetMapping("/getPreByRedis")
	public Map<String,Object> getPreByRedis(String pre,String delete){
		//批量获取key
		Set<String> keys = redisTemplate.keys(pre+"*");
		if(StringUtils.equals(delete,"delete")){
			for (String key : keys) {
				Long redisKey = redisTemplate.opsForValue().getOperations().getExpire(key);
				if (redisKey>12*3600*1000||redisKey==-1) {//大于12个小时或者不失效
					redisTemplate.delete(key);
				}
			}
		}
		return ResultUtils.getSuccessResult(keys);
	}



}
