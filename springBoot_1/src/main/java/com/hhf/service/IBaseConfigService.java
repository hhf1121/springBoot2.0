package com.hhf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhf.entity.BaseConfig;
import com.hhf.vo.BaseConfigVo;

import java.util.Map;

public interface IBaseConfigService extends IService<BaseConfig> {

    //根据编码获取配置信息
    Map<String, Object> getDataByConfigCode(String configCode);

    Map<String, Object> saveConfig(BaseConfigVo baseConfigVo);

    Map<String, Object> saveBaseConfig(BaseConfig baseConfigVo);

    Map<String, Object> queryBaseConfig();

    Map<String, Object> checkedBaseConfig(BaseConfig baseConfig);

    Map<String, Object> deleteBaseConfig(Long id);
}
