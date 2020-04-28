package com.hhf.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class BaseDistrict extends BaseDistrictKey {

    private String parentCode;

    private String name;

    private String mergerName;

    private String levelType;

    @TableLogic
    private Integer isDelete;

}