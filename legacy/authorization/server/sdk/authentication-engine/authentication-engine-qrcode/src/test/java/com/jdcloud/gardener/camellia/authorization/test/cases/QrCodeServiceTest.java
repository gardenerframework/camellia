package com.jdcloud.gardener.camellia.authorization.test.cases;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.UsernamePrincipal;
import com.jdcloud.gardener.camellia.authorization.qrcode.schema.QrCodeState;
import com.jdcloud.gardener.camellia.authorization.qrcode.service.QrCodeService;
import com.jdcloud.gardener.camellia.authorization.test.QrCodeTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/4/11 5:19 下午
 */
@SpringBootTest(classes = QrCodeTestApplication.class)
public class QrCodeServiceTest {
    @Autowired
    private QrCodeService qrCodeService;

    @Test
    @DisplayName("QrCode服务冒烟测试")
    public void simpleSmokeTest() throws InterruptedException {
        String token = qrCodeService.generateToken(20);
        Assertions.assertEquals(QrCodeState.WAIT_FOR_SCANNING, qrCodeService.readQrCodeState(token));
        qrCodeService.markQrCodeAsScanned(token, 20);
        Assertions.assertEquals(QrCodeState.WAIT_FOR_CONFIRMING, qrCodeService.readQrCodeState(token));
        qrCodeService.markQrCodeAsConfirmed(token, new UsernamePrincipal(UUID.randomUUID().toString()), 20);
        Assertions.assertEquals(QrCodeState.CONFIRMED, qrCodeService.readQrCodeState(token));
        BasicPrincipal user = qrCodeService.getPrincipal(token);
        Assertions.assertNotNull(user);
        Assertions.assertTrue(user instanceof UsernamePrincipal);
    }
}
