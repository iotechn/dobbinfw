package com.dobbinsoft.demo.runner;

import com.dobbinsoft.fw.support.annotation.EnableOpenPlatform;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(
        scanBasePackages = {
                "com.dobbinsoft.demo",
                "com.dobbinsoft.fw"
        },
        exclude = {
                RedisAutoConfiguration.class,
                RedisReactiveAutoConfiguration.class
        })
@MapperScan({
        "com.dobbinsoft.demo.data.mapper"
})
@EnableOpenPlatform
public class DemoRunnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoRunnerApplication.class, args);
    }

}
