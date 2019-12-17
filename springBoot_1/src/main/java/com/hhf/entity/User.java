package com.hhf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

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

    private Integer yes;

    @TableField("createDate")
    private LocalDateTime createDate;

    @TableField("picPath")
    private String picPath;


}
