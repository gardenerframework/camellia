package com.jdcloud.gardener.camellia.authorization.test.caese;

import com.jdcloud.gardener.camellia.authorization.test.EnterpriseWeChatAuthenticationEngineTestApplication;
import com.jdcloud.gardener.camellia.authorization.wechat.enterprise.client.EnterpriseWeChatClient;
import com.jdcloud.gardener.camellia.authorization.wechat.enterprise.configuration.EnterpriseWeChatOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ZhangHan
 * @date 2022/11/8 21:28
 */
@SpringBootTest(classes = EnterpriseWeChatAuthenticationEngineTestApplication.class)
public class EnterpriseWeChatClientTest {
    @Autowired
    private EnterpriseWeChatClient enterpriseWeChatClient;
    @Autowired
    private EnterpriseWeChatOption option;


    @Test
    public void smokeTest() {
        option.setCorpId("wwe406e174f04d8cf4");
        option.setAppSecret("tGVcyN7eB5rG_-XzlQDhObl2vqMZo6tecK1LGO8pi2s");
        //enterpriseWeChatClient.getUserId("6F6TfqhfOdO85GBzhMv0UsbOeAnldYLXJojWfgOYhtI");
    }
}
