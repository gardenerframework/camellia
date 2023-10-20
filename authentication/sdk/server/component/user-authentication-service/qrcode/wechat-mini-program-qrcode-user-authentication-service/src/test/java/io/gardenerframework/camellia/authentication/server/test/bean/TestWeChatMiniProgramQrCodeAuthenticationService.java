package io.gardenerframework.camellia.authentication.server.test.bean;

import io.gardenerframework.camellia.authentication.server.main.WeChatMiniProgramQrCodeAuthenticationService;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import javax.validation.Validator;

/**
 * @author zhanghan30
 * @date 2023/4/17 15:43
 */
@Component
public class TestWeChatMiniProgramQrCodeAuthenticationService extends WeChatMiniProgramQrCodeAuthenticationService<TestWeChatMiniProgramQrCodeService> {
    public TestWeChatMiniProgramQrCodeAuthenticationService(@NonNull Validator validator, @NonNull TestWeChatMiniProgramQrCodeService service) {
        super(validator, service);
    }
}
