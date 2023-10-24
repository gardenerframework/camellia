package io.gardenerframework.camellia.authorization.client.data.operation.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chris
 * @date 2023/10/24
 */
@SpringBootApplication
@MapperScan
public class ClientDataAtomicOperationTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientDataAtomicOperationTestApplication.class, args);
    }
}
