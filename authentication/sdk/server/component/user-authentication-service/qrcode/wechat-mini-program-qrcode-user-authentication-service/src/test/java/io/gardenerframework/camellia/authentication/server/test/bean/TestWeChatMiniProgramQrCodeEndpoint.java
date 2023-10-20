package io.gardenerframework.camellia.authentication.server.test.bean;

import io.gardenerframework.camellia.authentication.server.main.endpoint.WeChatMiniProgramQrCodeEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhanghan30
 * @date 2023/4/17 15:25
 */
@RestController
@RequestMapping("/test")
@Component
public class TestWeChatMiniProgramQrCodeEndpoint extends WeChatMiniProgramQrCodeEndpoint<TestCreateWeChatMiniProgramQrCodeRequest, TestWeChatMiniProgramQrCodeService> {
    public TestWeChatMiniProgramQrCodeEndpoint(TestWeChatMiniProgramQrCodeService service) {
        super(service);
    }
}
