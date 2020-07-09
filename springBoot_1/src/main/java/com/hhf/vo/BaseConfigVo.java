package com.hhf.vo;

import com.hhf.entity.BaseConfig;
import lombok.Data;

import java.util.List;

@Data
public class BaseConfigVo {

    private List<BaseConfig> lists;

    private String configCode;

}
