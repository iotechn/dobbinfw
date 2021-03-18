package com.dobbinsoft.demo.data.domain;

import lombok.Data;

import java.util.Date;

/**
 * Description: 管理员操作日志
 * User: rize
 * Date: 2020/8/11
 * Time: 15:44
 */
@Data
public class AdminLogDO {

    private Long id;

    private Long adminId;

    private Long requestId;

    private String apiGroup;

    private String apiMethod;

    private Date gmtCreate;

}
