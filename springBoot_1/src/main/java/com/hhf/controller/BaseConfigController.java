package com.hhf.controller;


import com.hhf.entity.BaseConfig;
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

    /**
     * 新增字典类型
     * @return
     */
    @PostMapping("/saveBaseConfig")
    public Map<String ,Object> saveBaseConfig(@RequestBody BaseConfig baseConfig){
        return baseConfigService.saveBaseConfig(baseConfig);
    }

    /**
     * 查询字典类型
     * @return
     */
    @GetMapping("/queryBaseConfig")
    public Map<String ,Object> queryBaseConfig(){
        return baseConfigService.queryBaseConfig();
    }


    /**
     * 删除字典类型
     * @return
     */
    @GetMapping("/deleteBaseConfig")
    public Map<String ,Object> deleteBaseConfig(Long id){
        return baseConfigService.deleteBaseConfig(id);
    }

    /**
     * 新增字典类型
     * @return
     */
    @PostMapping("/checkedBaseConfig")
    public Map<String ,Object> checkedBaseConfig(@RequestBody BaseConfig baseConfig){
        return baseConfigService.checkedBaseConfig(baseConfig);
    }


}
