package com.hhf.feignClient;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
public class PortalAgencyCenter implements Serializable {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 来源代办唯一标识
     */
    @Column(name = "source_sign")
    private String sourceSign;

    /**
     * 来源
     */
    @Column(name = "source_type")
    private String sourceType;

    /**
     * 调用方编码
     */
    @Column(name = "source_code")
    private String sourceCode;

    /**
     * 代办人工号
     */
    @Column(name = "agent_code")
    private String agentCode;

    /**
     * 接受部门(非必填，)
     */
    @Column(name = "agent_dept")
    private String agentDept;

    /**
     * 代办标题
     */
    @Column(name = "agency_title")
    private String agencyTitle;

    /**
     * 代办类型
     */
    @Column(name = "agency_type")
    private String agencyType;

    /**
     * 代办类别
     */
    @Column(name = "agency_category")
    private String agencyCategory;

    /**
     * 回调地址
     */
    @Column(name = "callback_url")
    private String callbackUrl;

    /**
     * 代办状态 1代办  2已完成
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 接受时间
     */
    @Column(name = "acceptance_time")
    private Date acceptanceTime;

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
     * 是否删除
     */
    @Column(name = "is_delete")
    private Integer isDelete;

    /**
     * 最后修改时间
     */
    @Column(name = "latest_time")
    private Date latestTime;
}