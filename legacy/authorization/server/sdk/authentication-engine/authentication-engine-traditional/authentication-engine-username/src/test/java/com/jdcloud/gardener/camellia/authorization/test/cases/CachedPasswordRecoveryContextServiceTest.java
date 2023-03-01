package com.jdcloud.gardener.camellia.authorization.test.cases;

import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeContextAccessor;
import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenDetails;
import com.jdcloud.gardener.camellia.authorization.test.UsernamePasswordPluginTestApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

/**
 * @author zhanghan30
 * @date 2022/4/11 3:21 下午
 */
@SpringBootTest(classes = UsernamePasswordPluginTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CachedPasswordRecoveryContextServiceTest {
    @Autowired
    private ChallengeContextAccessor challengeContextAccessor;
    @LocalServerPort
    private int port;
    @Autowired
    private AccessTokenDetails accessTokenDetails;

    @Test
    @DisplayName("客户密码寻回上下文服务冒烟测试")
    public void simpleSmokeTest() {
//        client.setPort(port);
//        String username = UUID.randomUUID().toString();
//        PasswordRecoveryChallengeResponse response = client.sendRecoveryRequest(new RecoverPasswordRequest(username, null));
//        ChallengeContext context = challengeContextAccessor.getContext(passwordRecoveryService, response.getChallengeId());
//        Assertions.assertNotNull(context);
//        String challengeId = response.getChallengeId();
//        //再来一次，查看重放
//        response = client.sendRecoveryRequest(new RecoverPasswordRequest(username, null));
//        Assertions.assertEquals(challengeId, response.getChallengeId());
//        client.responseChallenge(response.getChallengeId(), UUID.randomUUID().toString());
//        context = challengeContextAccessor.getContext(passwordRecoveryService, response.getChallengeId());
//        Assertions.assertTrue(context.isVerified());
//        client.resetPassword(response.getChallengeId(), UUID.randomUUID().toString());
//        Assertions.assertNull(challengeContextAccessor.getContext(passwordRecoveryService, response.getChallengeId()));
    }
}
