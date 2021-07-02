package com.dobbinarchetype.demo.data.domain;

import com.dobbinsoft.fw.support.domain.SuperDO;
import lombok.Data;

import java.util.Date;

/**
 * Created by rize on 2019/6/30.
 */
@Data
public class UserDO extends SuperDO {

    private String phone;

    private String password;

    /**
     * 需要扩展其他平台，使用横向扩展字段
     */
    private String wxMpOpenId;

    private String wxH5OpenId;

    private String wxAppOpenId;

    private String nickname;

    private String avatarUrl;

    private Integer level;

    private Date birthday;

    private Integer gender;

    private Date gmtLastLogin;

    private String lastLoginIp;

    private Integer status;

}
