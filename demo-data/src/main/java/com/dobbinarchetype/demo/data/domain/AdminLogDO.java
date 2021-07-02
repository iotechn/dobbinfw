package com.dobbinarchetype.demo.data.domain;

import com.dobbinsoft.fw.core.annotation.doc.ApiEntity;
import com.dobbinsoft.fw.core.annotation.doc.ApiField;
import lombok.Data;

import java.util.Date;

/**
 * Description: 管理员操作日志
 * User: rize
 * Date: 2020/8/11
 * Time: 15:44
 */
@Data
@ApiEntity(description = "管理员日志对象")
public class AdminLogDO {

    private Long id;

    @ApiField(description = "管理员ID")
    private Long adminId;

    @ApiField(description = "请求ID")
    private Long requestId;

    @ApiField(description = "API 分组")
    private String apiGroup;

    @ApiField(description = "API 方法")
    private String apiMethod;

    private Date gmtCreate;

}
