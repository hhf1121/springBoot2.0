package com.hhf.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Table(name = "sell_goods_photos")
public class SellGoodsPhotos  {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "id")
    @JsonSerialize(using = ToStringSerializer.class )
    private Long id;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    @JsonSerialize(using = ToStringSerializer.class )
    private Long goodsId;

    /**
     * 商品图片
     */
    @Column(name = "goods_photo")
    private String goodsPhoto;

    /**
     * 商品名称
     */
    @Column(name = "photo_name")
    private String photoName;


    /**
     * 是否展示
     */
    @Column(name = "is_show")
    private Integer isShow;

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

}