package io.gardenerframework.camellia.authentication.infra.challenge.engine.test.cases;

import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.CachedChallengeStore;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.test.ChallengeResponseEngineTestApplication;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/2/21 17:22
 */
@SpringBootTest(classes = ChallengeResponseEngineTestApplication.class)
public class CachedChallengeStoreTest {
    @Autowired
    private CachedChallengeStore challengeStore;

    @Test
    public void smokeTest() throws Exception {
        String applicationId = UUID.randomUUID().toString();
        String requestSignature = UUID.randomUUID().toString();
        ChallengeSubClass challenge = ChallengeSubClass.builder()
                .id(UUID.randomUUID().toString())
                .type(UUID.randomUUID().toString())
                //过期时间是10秒
                .expiryTime(new Date(new Date().getTime() + 100000))
                .field(UUID.randomUUID().toString())
                .build();
        challengeStore.saveChallenge(
                applicationId,
                CachedChallengeStoreTestScenario.class,
                requestSignature,
                challenge,
                Duration.between(
                        Instant.now(),
                        challenge.getExpiryTime().toInstant()
                )
        );
        String challengeId = challengeStore.getChallengeId(
                applicationId,
                CachedChallengeStoreTestScenario.class,
                requestSignature
        );
        Assertions.assertEquals(challenge.getId(), challengeId);
        ChallengeSubClass challengeSaved = (ChallengeSubClass) challengeStore.loadChallenge(
                applicationId,
                CachedChallengeStoreTestScenario.class,
                challenge.getId()
        );
        Assertions.assertInstanceOf(ChallengeSubClass.class, challenge);
        Assertions.assertEquals(challenge.getId(), challengeSaved.getId());
        Assertions.assertEquals(challenge.getType(), challengeSaved.getType());
        Assertions.assertEquals(challenge.getExpiryTime(), challengeSaved.getExpiryTime());
        Assertions.assertEquals(challenge.getField(), challengeSaved.getField());
        challengeStore.updateChallengeVerifiedFlag(
                applicationId,
                CachedChallengeStoreTestScenario.class,
                challenge.getId(),
                true,
                Duration.between(
                        Instant.now(),
                        challenge.getExpiryTime().toInstant()
                )
        );
        Assertions.assertTrue(challengeStore.isChallengeVerified(
                applicationId,
                CachedChallengeStoreTestScenario.class,
                challenge.getId()
        ));
        challengeStore.removeChallenge(
                applicationId,
                CachedChallengeStoreTestScenario.class,
                challenge.getId()
        );
        Assertions.assertNull(challengeStore.loadChallenge(
                applicationId,
                CachedChallengeStoreTestScenario.class,
                challenge.getId()
        ));
    }

    //子类自动可以被序列化
    @SuperBuilder
    @Getter
    @Setter
    public static class ChallengeSubClass extends Challenge {
        private String field;
    }

    public static class CachedChallengeStoreTestScenario implements Scenario {

    }
}
