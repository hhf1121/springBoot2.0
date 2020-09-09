package com.hhf.feignClient;

import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

/**
 *
 *调用公共服务，获取城市天气
 * http://www.weather.com.cn/data/sk/{cityId}.html
 *
 */
@FeignClient(value = CommonService.SERVICE_NAME, url = "http://www.weather.com.cn/data/sk/")
public interface CommonService {

    String SERVICE_NAME = "commonHomePortal";

    @RequestLine("GET {cityId}.html")
    String queryWeatherinfoByCityId(@Param("cityId") String cityId);
}
