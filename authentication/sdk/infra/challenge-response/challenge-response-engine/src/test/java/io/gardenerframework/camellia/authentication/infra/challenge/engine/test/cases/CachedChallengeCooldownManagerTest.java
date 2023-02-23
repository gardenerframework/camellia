package io.gardenerframework.camellia.authentication.infra.challenge.engine.test.cases;

import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.CachedChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.test.ChallengeResponseEngineTestApplication;
import io.gardenerframework.camellia.authentication.infra.client.schema.RequestingClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/2/21 18:14
 */
@SpringBootTest(classes = ChallengeResponseEngineTestApplication.class)
public class CachedChallengeCooldownManagerTest {
    @Autowired
    private CachedChallengeCooldownManager cooldownManager;

    @Test
    public void smokeTest() throws Exception {
        String appId = UUID.randomUUID().toString();
        RequestingClient client = RequestingClient.builder().clientId(appId).grantType(UUID.randomUUID().toString()).scopes(Collections.EMPTY_SET).build();
        String timerId = UUID.randomUUID().toString();
        Assertions.assertNull(cooldownManager.getTimeRemaining(client, CachedChallengeCooldownManagerTestScenario.class, timerId));
        //启动5米考cd
        Assertions.assertTrue(cooldownManager.startCooldown(client, CachedChallengeCooldownManagerTestScenario.class, timerId, Duration.ofSeconds(5)));
        Assertions.assertFalse(cooldownManager.startCooldown(client, CachedChallengeCooldownManagerTestScenario.class, timerId, Duration.ofSeconds(5)));
        //2秒后cd还没结束
        Thread.sleep(2000);
        Assertions.assertFalse(cooldownManager.startCooldown(client, CachedChallengeCooldownManagerTestScenario.class, timerId, Duration.ofSeconds(5)));
        Thread.sleep(5000);
        Assertions.assertTrue(cooldownManager.startCooldown(client, CachedChallengeCooldownManagerTestScenario.class, timerId, Duration.ofSeconds(5)));
        Thread.sleep(6000);
        Assertions.assertNull(cooldownManager.getTimeRemaining(client, CachedChallengeCooldownManagerTestScenario.class, timerId));
    }

    public static class CachedChallengeCooldownManagerTestScenario implements Scenario {

    }
}
