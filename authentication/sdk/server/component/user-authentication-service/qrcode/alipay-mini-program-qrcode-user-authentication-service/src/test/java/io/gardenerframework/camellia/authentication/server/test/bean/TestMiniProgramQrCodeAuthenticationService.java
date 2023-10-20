package io.gardenerframework.camellia.authentication.server.test.bean;

import io.gardenerframework.camellia.authentication.server.main.AlipayMiniProgramQrCodeAuthenticationService;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import javax.validation.Validator;

/**
 * @author zhanghan30
 * @date 2023/4/17 15:43
 */
@Component
public class TestMiniProgramQrCodeAuthenticationService extends AlipayMiniProgramQrCodeAuthenticationService<TestMiniProgramQrCodeService> {
    public TestMiniProgramQrCodeAuthenticationService(@NonNull Validator validator, @NonNull TestMiniProgramQrCodeService service) {
        super(validator, service);
    }
}
