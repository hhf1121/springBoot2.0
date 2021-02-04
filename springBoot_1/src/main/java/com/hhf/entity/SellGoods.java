package com.hhf.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ToString
@Table(name = "sell_goods")
public class SellGoods  {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 商品标题
     */
    @Column(name = "sell_title")
    private String sellTitle;

    /**
     * 商品内容
     */
    @Column(name = "sell_content")
    private String sellContent;

    /**
     * 商品类型1.家电2.衣物3.代步工具..
     */
    @Column(name = "sell_type")
    private Integer sellType;

    /**
     * 商品金额
     */
    @Column(name = "goods_fee")
    private BigDecimal goodsFee;

    /**
     * 商品类别1.求购2.销售
     */
    @Column(name = "sell_category")
    private Integer sellCategory;

    /**
     * 商品状态1.已买2.已卖3.下架
     */
    @Column(name = "sell_status")
    private Integer sellStatus;

    /**
     * 联系人编码
     */
    @Column(name = "user_code")
    private String userCode;

    /**
     * 联系人名字
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 电话
     */
    @Column(name = "user_phone")
    private String userPhone;

    /**
     * 地址
     */
    @Column(name = "user_address")
    private String userAddress;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 版本号
     */
    @Column(name = "version")
    private Integer version;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 创建人
     */
    @Column(name = "creater")
    private String creater;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 更新人
     */
    @Column(name = "updater")
    private String updater;

    /**
     * 数据最后变更时间
     */
    @Column(name = "latest_time")
    private Date latestTime;

    /**
     * 是否删除
     */
    @Column(name = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private String idStr;

    @TableField(exist = false)
    private Integer pageSize;

    @TableField(exist = false)
    private Integer pageIndex;

    @TableField(exist = false)
    private List<SellGoodsPhotos> sellGoodsPhotos;

}