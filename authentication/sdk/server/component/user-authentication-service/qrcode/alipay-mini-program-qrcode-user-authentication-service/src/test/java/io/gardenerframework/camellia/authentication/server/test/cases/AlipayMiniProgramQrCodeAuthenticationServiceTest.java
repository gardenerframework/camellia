package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.configuration.AlipayMiniProgramQrCodeAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.qrcode.AlipayMiniProgramQrCodeService;
import io.gardenerframework.camellia.authentication.server.test.AlipayMiniProgramQrCodeAuthenticationServiceTestApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhanghan30
 * @date 2023/3/16 19:32
 */
@SpringBootTest(classes = AlipayMiniProgramQrCodeAuthenticationServiceTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlipayMiniProgramQrCodeAuthenticationServiceTest {
    @Autowired
    private AlipayMiniProgramQrCodeService qrCodeService;

    @Autowired
    private AlipayMiniProgramQrCodeAuthenticationServiceOption option;

//    @Test
//    public void smokeTest() throws Exception {
//        CreateAlipayMiniProgramQrCodeRequest request = new TestCreateMiniProgramQrCodeRequest();
//        request.setSize(280);
//        request.setColor(0);
//        option.setPageUrl("test");
//        QrCodeService.QrCodeDetails qrCode = qrCodeService.create(request);
//        Assertions.assertEquals(QrCodeService.State.CREATED, qrCodeService.getState(qrCode.getCode()));
//        qrCodeService.changeState(qrCode.getCode(), QrCodeService.State.SCANNED);
//        Assertions.assertEquals(QrCodeService.State.SCANNED, qrCodeService.getState(qrCode.getCode()));
//        qrCodeService.changeState(qrCode.getCode(), QrCodeService.State.CONFIRMED);
//        Assertions.assertEquals(QrCodeService.State.CONFIRMED, qrCodeService.getState(qrCode.getCode()));
//        qrCode = qrCodeService.create(null);
//        Assertions.assertNull(qrCode.getImage());
//
//    }
}
