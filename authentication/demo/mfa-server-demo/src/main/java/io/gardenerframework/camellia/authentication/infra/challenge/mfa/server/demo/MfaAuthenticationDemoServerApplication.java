package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MfaAuthenticationDemoServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfaAuthenticationDemoServerApplication.class, args);
    }
}
