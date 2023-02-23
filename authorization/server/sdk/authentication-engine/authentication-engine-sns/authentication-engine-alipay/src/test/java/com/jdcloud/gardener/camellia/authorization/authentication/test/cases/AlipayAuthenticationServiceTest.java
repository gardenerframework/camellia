package com.jdcloud.gardener.camellia.authorization.authentication.test.cases;

import com.jdcloud.gardener.camellia.authorization.authentication.main.AlipayAuthenticationService;
import com.jdcloud.gardener.camellia.authorization.authentication.test.AlipayTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhanghan30
 * @date 2022/11/10 16:06
 */
@SpringBootTest(classes = AlipayTestApplication.class)
public class AlipayAuthenticationServiceTest {
    @Autowired
    private AlipayAuthenticationService alipayAuthenticationService;

    @Test
    public void smokeTest() {

    }
}
