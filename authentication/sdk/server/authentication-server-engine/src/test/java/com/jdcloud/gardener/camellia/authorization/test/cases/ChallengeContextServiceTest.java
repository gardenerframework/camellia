//package com.jdcloud.gardener.camellia.authorization.test.cases;
//
//import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeContextAccessor;
//import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeResponseService;
//import com.jdcloud.gardener.camellia.authorization.challenge.annotation.ChallengeId;
//import com.jdcloud.gardener.camellia.authorization.challenge.annotation.ValidateChallenge;
//import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.ChallengeCoolingDownException;
//import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.InvalidChallengeException;
//import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
//import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeContext;
//import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
//import com.jdcloud.gardener.camellia.authorization.common.api.group.AuthorizationServerRestController;
//import com.jdcloud.gardener.camellia.authorization.test.AuthorizationServerEngineTestApplication;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.context.annotation.Import;
//import org.springframework.lang.Nullable;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.Duration;
//import java.time.Instant;
//import java.util.Date;
//import java.util.UUID;
//
///**
// * @author ZhangHan
// * @date 2022/5/31 19:03
// */
//@SpringBootTest(classes = AuthorizationServerEngineTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DisplayName("ChallengeContextService测试")
//@Import({ChallengeContextServiceTest.TestingChallengeResponseService.class, ChallengeContextServiceTest.TestingChallengeResponseServiceController.class})
//public class ChallengeContextServiceTest {
//    @LocalServerPort
//    private int port;
//
//    @Test
//    @DisplayName("冒烟测试")
//    public void smokeTest() {
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getForObject("http://localhost:{port}/api/TestingChallengeResponseServiceController", void.class, port);
//    }
//
//    public static class TestingChallengeResponseService implements ChallengeResponseService<ChallengeRequest, Challenge> {
//
//        @Nullable
//        @Override
//        public Challenge sendChallenge(ChallengeRequest request) {
//            return new Challenge(request.getClientGroup(), "nan", Date.from(Instant.now().plus(Duration.ofSeconds(1000))), null);
//        }
//
//        @Override
//        public boolean validateResponse(String id, String response) throws InvalidChallengeException {
//            return true;
//        }
//
//        @Override
//        public void closeChallenge(String id) {
//
//        }
//
//        @ValidateChallenge
//        public void testingValidateResponseAnnotation(@ChallengeId String challengeId) {
//        }
//
//        @Nullable
//        @Override
//        public String getCooldownKey(ChallengeRequest request) {
//            return request.getClientGroup();
//        }
//
//        @Override
//        public long getCooldown() {
//            return 5;
//        }
//    }
//
//    @AuthorizationServerRestController
//    @RequestMapping("TestingChallengeResponseServiceController")
//    public static class TestingChallengeResponseServiceController {
//        @Autowired
//        private TestingChallengeResponseService testingChallengeResponseService;
//        @Autowired
//        private ChallengeContextAccessor accessor;
//
//        @GetMapping
//        public void test() throws InterruptedException {
//            String challengeId = UUID.randomUUID().toString();
//            Challenge test = testingChallengeResponseService.sendChallenge(new ChallengeRequest(new LinkedMultiValueMap<>(), challengeId, null, null));
//            Assertions.assertNotNull(test);
//            Assertions.assertEquals(challengeId, test.getId());
//            ChallengeContext context = accessor.getContext(testingChallengeResponseService, test.getId());
//            Assertions.assertNotNull(context);
//            //验证一下注解管不管用
//            Assertions.assertThrows(
//                    InvalidChallengeException.class,
//                    () -> testingChallengeResponseService.testingValidateResponseAnnotation(test.getId())
//            );
//            testingChallengeResponseService.validateResponse(test.getId(), UUID.randomUUID().toString());
//            context = accessor.getContext(testingChallengeResponseService, test.getId());
//            Assertions.assertNotNull(context);
//            //变为已验证
//            Assertions.assertTrue(context.isVerified());
//            //再验证一下注解是否有效
//            testingChallengeResponseService.testingValidateResponseAnnotation(test.getId());
//            //已经验证完成再次验证会失败
//            Assertions.assertThrows(
//                    InvalidChallengeException.class,
//                    () -> testingChallengeResponseService.validateResponse(test.getId(), UUID.randomUUID().toString())
//            );
//            //重新发送会进入cd
//            Assertions.assertThrows(
//                    ChallengeCoolingDownException.class,
//                    () -> testingChallengeResponseService.sendChallenge(new ChallengeRequest(new LinkedMultiValueMap<>(), test.getId(), null, null))
//            );
//            Thread.sleep(6000);
//            //会被判断为重新发送
//            testingChallengeResponseService.sendChallenge(new ChallengeRequest(new LinkedMultiValueMap<>(), test.getId(), null, null));
//            //重新发送时不会进入cd的
//            for (int i = 0; i < 100; i++) {
//                testingChallengeResponseService.sendChallenge(new ChallengeRequest(new LinkedMultiValueMap<>(), test.getId(), null, null));
//            }
//            //上下文就不会发生变化
//            ChallengeContext contextAfterResend = accessor.getContext(testingChallengeResponseService, test.getId());
//            Assertions.assertNotNull(contextAfterResend);
//            Assertions.assertEquals(context.getExpiresAt(), contextAfterResend.getExpiresAt());
//            Assertions.assertTrue(contextAfterResend.isVerified());
//            //关闭上下文
//            testingChallengeResponseService.closeChallenge(test.getId());
//            //然后就是空得了
//            context = accessor.getContext(testingChallengeResponseService, test.getId());
//            Assertions.assertNull(context);
//        }
//    }
//}
