package com.dobbinarchetype.demo.data.mapper;

import com.dobbinarchetype.demo.data.domain.UserDO;
import com.dobbinarchetype.demo.data.dto.UserDTO;
import com.dobbinsoft.fw.support.mapper.IMapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by rize on 2019/7/1.
 */
public interface UserMapper extends IMapper<UserDO> {

    public UserDTO login(@Param("phone") String phone, @Param("cryptPassword") String cryptPassword);

}
