package com.dobbinsoft.demo.data.dto;

import com.dobbinsoft.fw.core.entiy.SuperDTO;
import com.dobbinsoft.fw.core.entiy.inter.PermissionOwner;
import lombok.Data;

import java.util.List;

/**
 * Created by rize on 2019/4/8.
 */
@Data
public class AdminDTO extends SuperDTO implements PermissionOwner {
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
     * 管理员状态
     */
    private Integer status;

    private String lastLoginIp;

    private String gmtLastLogin;

    private List<String> roles;

    private List<Long> roleIds;

    private List<String> perms;

}
