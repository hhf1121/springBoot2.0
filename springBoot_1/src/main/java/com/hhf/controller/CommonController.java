package com.hhf.controller;


import com.hhf.feignClient.CommonService;
import com.hhf.utils.ResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("common/home/")
public class CommonController {


    @Autowired
    private CommonService commonService;


    @GetMapping("/queryWeatherinfo")
    public Map<String,Object> queryWeatherinfo(String cityId){
        if(StringUtils.isBlank(cityId)){
            return ResultUtils.getFailResult("cityId不能为空");
        }
        return ResultUtils.getSuccessResult(commonService.queryWeatherinfoByCityId(cityId));
    }



}
