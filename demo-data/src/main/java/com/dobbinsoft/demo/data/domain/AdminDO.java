package com.dobbinsoft.demo.data.domain;

import com.dobbinsoft.fw.support.domain.SuperDO;
import lombok.Data;

import java.util.Date;

/**
 * Created by rize on 2019/4/8.
 */
@Data
public class AdminDO extends SuperDO {

    /**
     * 管理员名
     */
    private String username;

    /**
     * 管理员登录密码
     */
    private String password;

    private String phone;

    private String realname;

    private String avatarUrl;

    /**
     * 管理员角色 JSON 数据
     */
    private String roleIds;

    /**
     * 管理员状态
     */
    private Integer status;

    private String lastLoginIp;

    private Date gmtLastLogin;

}
