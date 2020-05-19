package com.hhf.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

@Data
public class BaseMsg {

    private Long id;

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

}