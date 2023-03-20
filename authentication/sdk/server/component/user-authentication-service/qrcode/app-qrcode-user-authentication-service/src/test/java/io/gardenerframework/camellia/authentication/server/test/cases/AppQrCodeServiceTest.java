package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.main.qrcode.QrCodeService;
import io.gardenerframework.camellia.authentication.server.test.AppQrCodeServiceTestApplication;
import io.gardenerframework.camellia.authentication.server.test.bean.TestAppQrCodeService;
import io.gardenerframework.camellia.authentication.server.test.bean.TestCreateQrCodeRequest;
import io.gardenerframework.camellia.authentication.server.test.conf.TestAppQrCodeAuthenticationServiceOption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhanghan30
 * @date 2023/3/20 15:42
 */
@SpringBootTest(classes = AppQrCodeServiceTestApplication.class)
public class AppQrCodeServiceTest {
    @Autowired
    private TestAppQrCodeAuthenticationServiceOption option;
    @Autowired
    private TestAppQrCodeService qrCodeService;

    @Test
    public void smokeTest() throws Exception {
        option.setPageUrl("http://localhost");
        option.setLogoPath("qrcode/logo/jd.jpg");
        TestCreateQrCodeRequest request = new TestCreateQrCodeRequest();
        QrCodeService.QrCodeDetails qrCodeDetails = qrCodeService.create(request);
        Assertions.assertNotNull(qrCodeDetails.getImage());
    }
}
