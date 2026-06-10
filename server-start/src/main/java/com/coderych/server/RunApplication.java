package com.coderych.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 服务启动类
 */
@SpringBootApplication
@MapperScan("com.coderych.server.**.mapper")
public class RunApplication {
   public static void main(String[] args) {
        SpringApplication.run(RunApplication.class, args);
    }
}
