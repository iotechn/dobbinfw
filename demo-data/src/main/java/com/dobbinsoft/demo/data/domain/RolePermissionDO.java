package com.dobbinsoft.demo.data.domain;

import com.dobbinsoft.fw.support.domain.SuperDO;
import lombok.Data;

/**
 * Created by rize on 2019/4/8.
 */
@Data
public class RolePermissionDO extends SuperDO {

    private Long roleId;

    private String permission;

}
