package com.dobbinarchetype.demo.data.dto;

import com.dobbinarchetype.demo.data.domain.RoleDO;
import com.dobbinarchetype.demo.data.enums.AdminStatusType;
import com.dobbinsoft.fw.core.annotation.doc.ApiEntity;
import com.dobbinsoft.fw.core.annotation.doc.ApiField;
import com.dobbinsoft.fw.core.entiy.SuperDTO;
import com.dobbinsoft.fw.core.entiy.inter.PermissionOwner;
import lombok.Data;

import java.util.List;

/**
 * Created by rize on 2019/4/8.
 */
@Data
@ApiEntity(description = "管理员实体")
public class AdminDTO extends SuperDTO implements PermissionOwner {
    /**
     * 管理员名
     */
    @ApiField(description = "管理员用户名")
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
    @ApiField(description = "状态", enums = AdminStatusType.class)
    private Integer status;

    private String lastLoginIp;

    private String gmtLastLogin;

    private List<RoleDO> role;

    private List<String> roles;

    private List<Long> roleIds;

    private List<String> perms;

}
