package io.gardenerframework.camellia.authentication.infra.challenge.engine.test;

import io.gardenerframework.camellia.authentication.infra.challenge.engine.test.cases.AbstractChallengeResponseServiceTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2023/2/21 17:21
 */
@SpringBootApplication
@Import(AbstractChallengeResponseServiceTest.TestChallengeResponseService.class)
public class ChallengeResponseEngineTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChallengeResponseEngineTestApplication.class, args);
    }
}
