package com.hhf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BaseMsg {

    @TableId(value ="id",type = IdType.AUTO)//自增id
    private Long id;

    private String userName;

    private Integer fromId;

    private Integer toId;

    private String msg;

    @TableLogic
    private Integer isDelete;

    private Integer status;

    private Date lastTime;


    //当前页
    @TableField(exist = false)
    private Integer pageIndex;

    //页面条数
    @TableField(exist = false)
    private Integer pageSize;

    //对方名称
    @TableField(exist = false)
    private String fromName;

    //对方名称
    @TableField(exist = false)
    private String toName;

    //请求类型
    @TableField(exist = false)
    private String type;

    @TableField(exist = false)
    List<String> ids;

}