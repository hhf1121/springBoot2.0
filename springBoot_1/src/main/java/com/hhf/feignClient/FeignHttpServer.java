package com.hhf.feignClient;

import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Map;

//如果有url，走url。
//如果没有url，走微服务名（被调用服务需注册到注册中心）
//@RequestLine可使用url的方式。@RequestMapping使用微服务的方式
@FeignClient(value = "provider-service",url = "http://192.168.202.53:9080/")
public interface FeignHttpServer {

    @RequestLine("POST /portalApi/agencyCenter/saveAgency")
    Map<String,Object> sendAgency (PortalAgencyCenterDto portalAgencyCenterDto);

    //get方式
    @RequestLine("GET /internal/queryDeptBaseInfoByPage?currentPage={currentPage}&pageSize={pageSize}&param={param}")
    String queryDeptBaseInfoByPage(@Param("currentPage")Integer currentPage, @Param("pageSize") Integer pageSize, @Param("param") String param);


    @RequestLine("GET /getDataByFeign/{yes}")
    public Map<String,Object> getDataByFeign(@Param("yes") Integer yes);


}
