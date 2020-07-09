package com.hhf.controller;


import com.hhf.service.IBaseConfigService;
import com.hhf.utils.ResultUtils;
import com.hhf.vo.BaseConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/baseConfig")
public class BaseConfigController {


    @Autowired
    private IBaseConfigService baseConfigService;


    @GetMapping("/test")
    public Map<String,Object> test(){
        return ResultUtils.getSuccessResult("成功");
    }

    @GetMapping("/getDataByConfigCode")
    public Map<String ,Object> getDataByConfigCode(String configCode){
        return baseConfigService.getDataByConfigCode(configCode);
    }


    @PostMapping("/saveConfig")
    public Map<String ,Object> saveConfig(@RequestBody BaseConfigVo baseConfigVo){
        return baseConfigService.saveConfig(baseConfigVo);
    }


}
