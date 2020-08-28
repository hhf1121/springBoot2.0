package com.hhf.feignClient;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class PortalAgencyCenterDto extends PortalAgencyCenter {

    private static final long serialVersionUID = 1L;
    //访问密钥
    private String accessKey;
    //代办接受类型  1：到人，2：到部门，3：到组织类型
    private Integer acceptanceType;
    //代办类型为部门或组织类型是，是否递归查找其下部门人员  0:否  1：是  默认为0
    private Integer isRecursion;
    //是否一人办理  0:否  1:是   acceptanceType 为 2，3时使用。但一人代办，该代办所有人状态都变为以办
//    private Integer isOnlyOne;
    //代办人，可传多个。该集合有值时使用改字段。否则使用agentCode
    private List<String> agentCodes;
    //门户首页展示代办条数  为null则默认为5 最小0，最大500
    private Integer count;

    //id
    private String idStr;

    //分页信息：
    private Integer pageSize;

    private Integer currentPage;

    //页面查询
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
