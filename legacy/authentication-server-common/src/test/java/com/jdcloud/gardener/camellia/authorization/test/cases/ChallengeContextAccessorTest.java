package com.jdcloud.gardener.camellia.authorization.test.cases;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeContextAccessor;
import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeResponseService;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.InvalidChallengeException;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeContext;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.test.AuthorizationServerCommonTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author ZhangHan
 * @date 2022/6/1 13:56
 */
@SpringBootTest(classes = AuthorizationServerCommonTestApplication.class)
public class ChallengeContextAccessorTest {

    @Test
    public void smokeTest() {
        TestingChallengeContextAccessor challengeContextAccessor = new TestingChallengeContextAccessor();
        challengeContextAccessor.getContext(ChallengeResponseServiceWithDefaultRequest.class, UUID.randomUUID().toString());
        challengeContextAccessor.getContext(new ChallengeResponseServiceWithDefaultRequest(), UUID.randomUUID().toString());
        challengeContextAccessor.getContext(ChallengeResponseServiceWithSubclassRequest.class, UUID.randomUUID().toString());
        challengeContextAccessor.getContext(new ChallengeResponseServiceWithSubclassRequest(), UUID.randomUUID().toString());
    }

    public static class ChallengeResponseServiceWithDefaultRequest implements ChallengeResponseService<ChallengeRequest, Challenge> {
        @Nullable
        @Override
        public String getCooldownKey(ChallengeRequest request) {
            return null;
        }

        @Override
        public long getCooldown() {
            return 0;
        }

        @Nullable
        @Override
        public Challenge sendChallenge(ChallengeRequest request) {
            return null;
        }

        @Override
        public boolean validateResponse(String id, String response) throws InvalidChallengeException {
            return false;
        }

        @Override
        public void closeChallenge(String id) {

        }
    }

    public static class SubclassChallengeRequest extends ChallengeRequest {

        public SubclassChallengeRequest(MultiValueMap<String, String> headers, String clientGroup, @Nullable Client client, @Nullable User user) {
            super(headers, clientGroup, client, user);
        }
    }

    public static class SubclassChallenge extends Challenge {

        public SubclassChallenge(String id, String authenticator, Date expiresAt, Map<String, String> parameters) {
            super(id, authenticator, expiresAt, parameters);
        }
    }

    public static class ChallengeResponseServiceWithSubclassRequest implements ChallengeResponseService<SubclassChallengeRequest, SubclassChallenge> {
        @Nullable
        @Override
        public String getCooldownKey(SubclassChallengeRequest request) {
            return null;
        }

        @Override
        public long getCooldown() {
            return 0;
        }

        @Nullable
        @Override
        public SubclassChallenge sendChallenge(SubclassChallengeRequest request) {
            return null;
        }

        @Override
        public boolean validateResponse(String id, String response) throws InvalidChallengeException {
            return false;
        }

        @Override
        public void closeChallenge(String id) {

        }
    }

    public static class TestingChallengeContextAccessor implements ChallengeContextAccessor {

        @Nullable
        @Override
        public ChallengeContext getContext(Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>> clazz, String challengeId) {
            return null;
        }
    }
}
