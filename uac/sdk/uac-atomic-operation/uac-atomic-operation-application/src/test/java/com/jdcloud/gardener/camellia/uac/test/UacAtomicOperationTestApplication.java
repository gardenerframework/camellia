package com.jdcloud.gardener.camellia.uac.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhanghan30
 * @date 2022/9/17 12:41 上午
 */
@SpringBootApplication
@MapperScan
public class UacAtomicOperationTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(UacAtomicOperationTestApplication.class, args);
    }
}
