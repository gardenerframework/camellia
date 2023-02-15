package io.gardenerframework.camellia.authentication.infra.sms.engine.test.cases;

import io.gardenerframework.camellia.authentication.infra.sms.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.sms.engine.exceptions.SmsAuthenticationInCooldownException;
import io.gardenerframework.camellia.authentication.infra.sms.engine.service.SmsAuthenticationService;
import io.gardenerframework.camellia.authentication.infra.sms.engine.test.SmsAuthenticationEngineTestApplication;
import io.gardenerframework.camellia.authentication.infra.sms.engine.test.utils.SmsAuthenticationEventListener;
import io.gardenerframework.camellia.authentication.infra.sms.engine.test.utils.SmsAuthenticationTestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/2/15 15:24
 */
@SpringBootTest(classes = SmsAuthenticationEngineTestApplication.class)
public class SmsAuthenticationEngineTest {
    @Autowired
    private SmsAuthenticationService smsAuthenticationService;

    @Autowired
    private SmsAuthenticationTestClient smsAuthenticationTestClient;

    @Autowired
    private SmsAuthenticationEventListener smsAuthenticationEventListener;

    @Test
    public void test() throws InterruptedException {
        String applicationId = UUID.randomUUID().toString();
        String mobilePhoneNumber = UUID.randomUUID().toString();
        String code = UUID.randomUUID().toString();
        List<Thread> threads = new LinkedList<>();
        smsAuthenticationTestClient.resetCounter();
        for (int i = 0; i < 100; i++) {
            //模拟并发，只有1个能发送成功
            Thread thread = new Thread(
                    () -> smsAuthenticationService.sendCode(
                            applicationId,
                            mobilePhoneNumber,
                            TestScene.class,
                            code,
                            Duration.ofSeconds(10L)
                    )
            );
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.join();
        }
        //只会调用发送端一次
        Assertions.assertEquals(1, smsAuthenticationTestClient.getCounter().get());
        //检查存储是否正确
        Assertions.assertTrue(smsAuthenticationService.verifyCode(
                applicationId,
                mobilePhoneNumber,
                TestScene.class,
                code
        ));
        Assertions.assertTrue(
                smsAuthenticationEventListener.isAboutToSend()
        );
        Assertions.assertTrue(
                smsAuthenticationEventListener.isSent()
        );
        //再次发送
        Assertions.assertThrowsExactly(
                SmsAuthenticationInCooldownException.class,
                () -> smsAuthenticationService.sendCode(
                        applicationId,
                        mobilePhoneNumber,
                        TestScene.class,
                        code,
                        Duration.ofSeconds(10L)
                )
        );
        //10秒后
        Thread.sleep(10000L);
        //重新生成
        String newCode = UUID.randomUUID().toString();
        //发送成功
        smsAuthenticationService.sendCode(
                applicationId,
                mobilePhoneNumber,
                TestScene.class,
                newCode,
                Duration.ofSeconds(10L)
        );
    }

    public static class TestScene implements Scenario {

    }
}
