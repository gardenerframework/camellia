package com.jdcloud.gardener.camellia.uac.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhanghan30
 * @date 2022/8/17 9:21 下午
 */
@SpringBootApplication
@MapperScan
public class AccountApiEngineTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountApiEngineTestApplication.class, args);
    }
}