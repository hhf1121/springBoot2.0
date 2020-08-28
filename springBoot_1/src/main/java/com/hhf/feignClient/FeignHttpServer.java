package com.hhf.feignClient;

import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Map;

@FeignClient(value = "ishttp",url = "http://192.168.202.53:9080/")
public interface FeignHttpServer {

    @RequestLine("POST portal/agencyCenter/saveAgency")
    Map<String,Object> sendAgency (@Param("portalAgencyCenterDto") PortalAgencyCenterDto portalAgencyCenterDto);

}
