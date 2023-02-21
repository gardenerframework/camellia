package io.gardenerframework.camellia.authentication.infra.challenge.engine.test.cases;

import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.CachedChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.test.ChallengeResponseEngineTestApplication;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/2/21 18:07
 */
@SpringBootTest(classes = ChallengeResponseEngineTestApplication.class)
public class CachedChallengeContextStoreTest {
    @Autowired
    private CachedChallengeContextStore contextStore;

    @Test
    public void smokeTest() throws Exception {
        String appId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        ChallengeContextSubClass context = ChallengeContextSubClass.builder()
                .field(UUID.randomUUID().toString())
                .build();
        contextStore.saveContext(
                appId,
                CachedChallengeContextStoreTestScenario.class,
                challengeId,
                context,
                Duration.ofSeconds(10)
        );
        ChallengeContextSubClass contextSaved = (ChallengeContextSubClass) contextStore.loadContext(
                appId,
                CachedChallengeContextStoreTestScenario.class,
                challengeId
        );
        Assertions.assertInstanceOf(ChallengeContextSubClass.class, contextSaved);
        Assertions.assertEquals(context.getField(), contextSaved.getField());
        contextStore.removeContext(
                appId,
                CachedChallengeContextStoreTestScenario.class,
                challengeId
        );
        Assertions.assertNull(
                contextStore.loadContext(
                        appId,
                        CachedChallengeContextStoreTestScenario.class,
                        challengeId
                )
        );
    }

    @SuperBuilder
    @Getter
    @Setter
    public static class ChallengeContextSubClass implements ChallengeContext {
        private String field;
    }

    public static class CachedChallengeContextStoreTestScenario implements Scenario {

    }
}
