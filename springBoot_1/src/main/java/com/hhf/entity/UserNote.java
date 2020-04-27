package com.hhf.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class UserNote {

    @TableId(value ="id",type = IdType.AUTO)//自增id
    private Long id;

    private String noteTitle;

    private Integer noteType;

    private String imgCode;

    private String noteAddress;

    private String noteName;

    private String noteRemark;

    private BigDecimal noteMoney;

    @TableField(fill = FieldFill.INSERT)
    private Date createrTime;

    @TableField(fill = FieldFill.INSERT)
    private String creater;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifierTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String modifier;

//    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer recordVersion;

    @TableLogic
    private Integer isDelete;

    //当前页
    @TableField(exist = false)
    private Integer pageIndex;

    //页面条数
    @TableField(exist = false)
    private Integer pageSize;

    //ids
    @TableField(exist = false)
    private List<String> ids;

    @TableField(exist = false)
    private String idStr;

    @TableField(exist = false)
    private String timeStr;//返回vo的日期年月日


}