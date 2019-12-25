package com.hhf.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserNote {

    @TableId
    private Long id;

    private String noteTitle;

    private Integer noteType;

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

}