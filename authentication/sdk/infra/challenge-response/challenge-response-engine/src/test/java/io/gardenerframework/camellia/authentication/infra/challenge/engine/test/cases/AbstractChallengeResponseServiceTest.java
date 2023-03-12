package io.gardenerframework.camellia.authentication.infra.challenge.engine.test.cases;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeAuthenticatorNameProvider;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.SaveInChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.AbstractChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeStore;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.test.ChallengeResponseEngineTestApplication;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/2/23 10:31
 */
@SpringBootTest(classes = ChallengeResponseEngineTestApplication.class)
public class AbstractChallengeResponseServiceTest {
    @Autowired
    private TestChallengeResponseService testChallengeResponseService;

    @Autowired
    private GenericCachedChallengeStore challengeStore;

    @Test
    public void smokeTest() throws Exception {
        String appId = UUID.randomUUID().toString();
        RequestingClient client = OAuth2RequestingClient.builder().clientId(appId).grantType(UUID.randomUUID().toString()).scopes(Collections.EMPTY_SET).build();
        String requestId = UUID.randomUUID().toString();
        TestChallengeRequest request = TestChallengeRequest.builder().requestId(requestId).saveInContext(UUID.randomUUID().toString()).build();
        Challenge challenge = testChallengeResponseService.sendChallenge(
                client,
                AbstractChallengeResponseServiceTestScenario.class,
                request
        );
        //读取上下文
        TestChallengeContext context = testChallengeResponseService.getContext(
                client,
                AbstractChallengeResponseServiceTestScenario.class,
                challenge.getId()
        );
        Assertions.assertEquals(
                request.getSaveInContext(),
                context.getSaveInContext()
        );
        //10秒cd
        Assertions.assertThrowsExactly(
                ChallengeInCooldownException.class,
                () -> testChallengeResponseService.sendChallenge(
                        client,
                        AbstractChallengeResponseServiceTestScenario.class,
                        TestChallengeRequest.builder().requestId(requestId).build()
                )
        );
        Thread.sleep(10000);
        //再次发送成功
        challenge = testChallengeResponseService.sendChallenge(
                client,
                AbstractChallengeResponseServiceTestScenario.class,
                TestChallengeRequest.builder().requestId(requestId).build()
        );
        String response = challenge.getId();
        TestChallengeContext testChallengeContext = testChallengeResponseService.getContext(client, AbstractChallengeResponseServiceTestScenario.class, challenge.getId());
        Assertions.assertEquals(response, testChallengeContext.getResponse());
        //完成验证
        Assertions.assertTrue(testChallengeResponseService.verifyResponse(
                client,
                AbstractChallengeResponseServiceTestScenario.class,
                challenge.getId(),
                response
        ));
        //bj
        Assertions.assertFalse(testChallengeResponseService.verifyResponse(
                client,
                AbstractChallengeResponseServiceTestScenario.class,
                challenge.getId() + UUID.randomUUID().toString(),
                response
        ));
        //释放资源
        testChallengeResponseService.closeChallenge(
                client,
                AbstractChallengeResponseServiceTestScenario.class,
                challenge.getId()
        );
        //上下文就已经消失
        Assertions.assertNull(
                testChallengeResponseService.getContext(
                        client,
                        AbstractChallengeResponseServiceTestScenario.class,
                        challenge.getId()
                )
        );
        //挑战存储也已经消失
        Assertions.assertNull(
                challengeStore.loadChallenge(
                        client,
                        AbstractChallengeResponseServiceTestScenario.class,
                        challenge.getId()
                )
        );
    }

    public static class AbstractChallengeResponseServiceTestScenario implements Scenario {
    }

    public static class TestChallengeResponseService extends AbstractChallengeResponseService<
            TestChallengeRequest,
            Challenge,
            TestChallengeContext> implements ChallengeAuthenticatorNameProvider {

        public TestChallengeResponseService(@NonNull GenericCachedChallengeStore challengeStore, @NonNull ChallengeCooldownManager challengeCooldownManager, @NonNull GenericCachedChallengeContextStore challengeContextStore) {
            super(challengeStore, challengeCooldownManager, challengeContextStore.migrateType());
        }

        @Override
        protected boolean replayChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull TestChallengeRequest request) {
            return false;
        }

        @Override
        protected @NonNull String getRequestSignature(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull TestChallengeRequest request) {
            return "";
        }

        @Override
        protected boolean hasCooldown(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull TestChallengeRequest request) {
            return true;
        }

        @Override
        protected @NonNull String getCooldownTimerId(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull TestChallengeRequest request) {
            return request.getRequestId();
        }

        @Override
        protected int getCooldownTime(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull TestChallengeRequest request) {
            return 10;
        }

        @Override
        protected Challenge sendChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull TestChallengeRequest request, @NonNull Map<String, Object> payload) throws Exception {
            return Challenge.builder()
                    .id(UUID.randomUUID().toString())
                    .expiryTime(Date.from(Instant.now().plus(Duration.ofSeconds(300))))
                    .build();
        }

        @Override
        protected TestChallengeContext createContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull TestChallengeRequest request, @NonNull Challenge challenge, @NonNull Map<String, Object> payload) {
            return new TestChallengeContext(challenge.getId(), null);
        }

        @Override
        protected boolean verifyChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId, @NonNull TestChallengeContext context, @NonNull String response) throws Exception {
            return getContext(client, scenario, challengeId).getResponse().equals(response);
        }

        @Override
        public @NonNull String getChallengeAuthenticatorName() {
            return "test";
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    public static class TestChallengeRequest implements ChallengeRequest {
        @NonNull
        private String requestId;
        @SaveInChallengeContext
        private String saveInContext;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestChallengeContext implements ChallengeContext {
        @NonNull
        private String response;
        private String saveInContext;
    }
}
