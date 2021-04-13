package com.dobbinsoft.demo.data.config;

import com.dobbinsoft.demo.data.dto.AdminDTO;
import com.dobbinsoft.demo.data.dto.UserDTO;
import com.dobbinsoft.fw.core.util.SessionUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: SessionConfig
 * Description: TODO
 *
 * @author: e-weichaozheng
 * @date: 2021-03-18
 */
@Configuration
public class SessionConfig {

    @Bean
    public SessionUtil<UserDTO, AdminDTO> sessionUtil() {
        return new SessionUtil<>(UserDTO.class, AdminDTO.class);
    }

}
