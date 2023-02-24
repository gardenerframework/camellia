package io.gardenerframework.camellia.authentication.infra.challenge.engine.test.cases;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.CachedChallengeContextStoreTemplate;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.test.ChallengeResponseEngineTestApplication;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/2/21 18:07
 */
@SpringBootTest(classes = ChallengeResponseEngineTestApplication.class)
public class CachedChallengeContextStoreTest {
    @Autowired
    private CachedChallengeContextStoreTemplate<ChallengeContext> contextStore;

    @Test
    public void smokeTest() throws Exception {
        String appId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        RequestingClient client = OAuth2RequestingClient.builder().clientId(appId).grantType(UUID.randomUUID().toString()).scopes(Collections.EMPTY_SET).build();
        ChallengeContextSubClass context = ChallengeContextSubClass.builder()
                .field(UUID.randomUUID().toString())
                .build();
        contextStore.saveContext(
                client,
                CachedChallengeContextStoreTestScenario.class,
                challengeId,
                context,
                Duration.ofSeconds(10)
        );
        ChallengeContextSubClass contextSaved = (ChallengeContextSubClass) contextStore.loadContext(
                client,
                CachedChallengeContextStoreTestScenario.class,
                challengeId
        );
        Assertions.assertInstanceOf(ChallengeContextSubClass.class, contextSaved);
        Assertions.assertEquals(context.getField(), contextSaved.getField());
        contextStore.removeContext(
                client,
                CachedChallengeContextStoreTestScenario.class,
                challengeId
        );
        Assertions.assertNull(
                contextStore.loadContext(
                        client,
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
