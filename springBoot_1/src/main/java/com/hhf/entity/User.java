package com.hhf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * <p>
 * 
 * </p>
 *
 * @author xubulong2
 * @since 2019-12-17
 */
@Data
@Accessors(chain = true)
public class User {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("userName")
    private String userName;

    @TableField("passWord")
    private String passWord;

    private String name;

    private String address;

    @TableField("photoData")
    private byte [] photoData;

    private Integer yes;

    //以此格式来接收前端传入的时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("createDate")
    private Date createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    @TableField("brithday")
    private Date brithday;//生日

    @TableField("picPath")
    private String picPath;

    @TableField(exist = false)
    private Integer pageIndex;

    @TableField(exist = false)
    private Integer pageSize;

    @TableLogic
    @TableField("isDelete")
    private Integer isDelete;

    @TableField(exist = false)
    private String cachePhoto;

    @TableField(exist = false)
    private String value;

    @TableField(exist = false)
    private String isOnLine;

}
