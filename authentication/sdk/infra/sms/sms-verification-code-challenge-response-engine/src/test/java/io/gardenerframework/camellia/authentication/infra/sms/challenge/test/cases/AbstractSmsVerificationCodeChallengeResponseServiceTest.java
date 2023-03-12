package io.gardenerframework.camellia.authentication.infra.sms.challenge.test.cases;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.DefaultSmsVerificationCodeChallengeContext;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.DefaultSmsVerificationCodeChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeContext;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.test.SmsVerificationCodeChallengeResponseServiceTestApplication;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import io.gardenerframework.fragrans.data.cache.manager.annotation.Cached;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Serializable;
import java.time.Duration;
import java.util.UUID;

@SpringBootTest(classes = SmsVerificationCodeChallengeResponseServiceTestApplication.class)
public class AbstractSmsVerificationCodeChallengeResponseServiceTest {
    @Autowired
    private CacheClient client;

    @Test
    public void smokeTest() {
        SomeOtherChallengeRequest someOtherChallengeRequest = new SomeOtherChallengeRequest();
        someOtherChallengeRequest.setFiled("");
        someOtherChallengeRequest.setMobilePhoneNumber("");
        BasicCacheManager<SomeContext> cacheManager = new BasicCacheManager<SomeContext>(client) {
        };
        SomeContext someContext = new SomeContext();
        String id = UUID.randomUUID().toString();
        someContext.setCode(UUID.randomUUID().toString());
        someContext.setHaha(UUID.randomUUID().toString());
        cacheManager.set(id, someContext, Duration.ofSeconds(30));
        Assertions.assertEquals(someContext.getCode(), cacheManager.get(id).getCode());
    }

    @SuperBuilder
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SomeOtherChallengeRequest implements ChallengeRequest, SmsVerificationCodeChallengeRequest, Serializable {
        private String filed;
        @Delegate
        @Getter(AccessLevel.NONE)
        private final SmsVerificationCodeChallengeRequest smsVerificationCodeChallengeRequest = new DefaultSmsVerificationCodeChallengeRequest();
    }

    @NoArgsConstructor
    @Getter
    @Setter
    @Cached(namespaces = "haha", suffix = "hehe")
    public static class SomeContext implements ChallengeContext, SmsVerificationCodeChallengeContext {
        private String haha;
        @Delegate
        @Getter(AccessLevel.NONE)
        private final SmsVerificationCodeChallengeContext smsVerificationCodeChallengeContext = new DefaultSmsVerificationCodeChallengeContext();
    }
}
