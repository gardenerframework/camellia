package com.jdcloud.gardener.camellia.authorization.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author ZhangHan
 * @date 2022/11/8 21:26
 */
@SpringBootApplication
public class EnterpriseWeChatAuthenticationEngineTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnterpriseWeChatAuthenticationEngineTestApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
