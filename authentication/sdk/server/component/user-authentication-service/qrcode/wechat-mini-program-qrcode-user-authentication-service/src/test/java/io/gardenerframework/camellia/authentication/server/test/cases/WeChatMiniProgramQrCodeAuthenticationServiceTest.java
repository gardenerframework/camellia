package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.configuration.WeChatMiniProgramQrCodeAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.qrcode.QrCodeService;
import io.gardenerframework.camellia.authentication.server.main.qrcode.WeChatMiniProgramQrCodeService;
import io.gardenerframework.camellia.authentication.server.main.schema.request.CreateWeChatMiniProgramQrCodeRequest;
import io.gardenerframework.camellia.authentication.server.test.WeChatMiniProgramQrCodeAuthenticationServiceTestApplication;
import org.junit.jupiter.api.Assertions;
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

    @Autowired
    private WeChatMiniProgramQrCodeAuthenticationServiceOption option;

    @Test
    public void smokeTest() throws Exception {
        CreateWeChatMiniProgramQrCodeRequest request = new CreateWeChatMiniProgramQrCodeRequest();
        request.setSize(280);
        request.setColor(0);
        //不存在的页面会报错
//        option.setLandingPageUrl("hehe/haha");
        QrCodeService.QrCodeDetails qrCode = weChatMiniProgramQrCodeService.create(request);
        Assertions.assertEquals(QrCodeService.State.CREATED, weChatMiniProgramQrCodeService.getState(qrCode.getCode()));
        weChatMiniProgramQrCodeService.changeState(qrCode.getCode(), QrCodeService.State.SCANNED);
        Assertions.assertEquals(QrCodeService.State.SCANNED, weChatMiniProgramQrCodeService.getState(qrCode.getCode()));
        weChatMiniProgramQrCodeService.changeState(qrCode.getCode(), QrCodeService.State.CONFIRMED);
        Assertions.assertEquals(QrCodeService.State.CONFIRMED, weChatMiniProgramQrCodeService.getState(qrCode.getCode()));
         qrCode = weChatMiniProgramQrCodeService.create(null);
         Assertions.assertNull(qrCode.getImage());

    }
}
