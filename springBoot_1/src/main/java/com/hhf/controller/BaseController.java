package com.hhf.controller;


import com.hhf.service.IBaseService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/base")
public class BaseController {

    @Autowired
    private IBaseService baseService;

    /**
     * 根据编码和等级查询行政信息
     * @param code
     * @param level
     * @return
     */
    @RequestMapping("/getDistrict")
    public Map<String,Object> getDistrict(String code, String level){
        return baseService.getDistrict(code,level);
    }

    /**
     * 联动选择器、根据等级、一次性拉取数据
     * @param level
     * @return
     */
    @RequestMapping("/getSelectDistrictByLevel")
    public Map<String,Object> getSelectDistrict(String level) throws ExecutionException, InterruptedException {
        return baseService.getSelectDistrictByLevel(level);
    }

}
