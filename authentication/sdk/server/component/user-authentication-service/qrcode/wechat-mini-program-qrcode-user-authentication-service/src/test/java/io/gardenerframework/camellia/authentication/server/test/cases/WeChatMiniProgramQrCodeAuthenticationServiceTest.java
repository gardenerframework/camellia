package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.main.qrcode.WeChatMiniProgramQrCodeService;
import io.gardenerframework.camellia.authentication.server.main.schema.request.CreateWeChatMiniProgramQrCodeRequest;
import io.gardenerframework.camellia.authentication.server.test.WeChatMiniProgramQrCodeAuthenticationServiceTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhanghan30
 * @date 2023/3/16 19:32
 */
@SpringBootTest(classes = WeChatMiniProgramQrCodeAuthenticationServiceTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeChatMiniProgramQrCodeAuthenticationServiceTest {
    @Autowired
    private WeChatMiniProgramQrCodeService weChatMiniProgramQrCodeService;

    @Test
    public void smokeTest() throws Exception {
        CreateWeChatMiniProgramQrCodeRequest request = new CreateWeChatMiniProgramQrCodeRequest();
        request.setSize(280);
        request.setColor(0);
        weChatMiniProgramQrCodeService.createCode(request);
    }
}
