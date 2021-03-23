package com.dobbinsoft.demo.data.domain;

import com.dobbinsoft.fw.core.annotation.doc.ApiEntity;
import com.dobbinsoft.fw.support.domain.SuperDO;
import lombok.Data;

/**
 * Created by rize on 2019/4/8.
 */
@Data
@ApiEntity(description = "角色领域模型")
public class RoleDO extends SuperDO {

    private String title;

    private String description;

    private Integer status;

}
