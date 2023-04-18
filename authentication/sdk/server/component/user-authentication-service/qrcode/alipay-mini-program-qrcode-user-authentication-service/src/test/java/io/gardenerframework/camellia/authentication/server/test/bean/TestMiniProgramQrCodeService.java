package io.gardenerframework.camellia.authentication.server.test.bean;

import io.gardenerframework.camellia.authentication.server.main.qrcode.AlipayMiniProgramQrCodeService;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhanghan30
 * @date 2023/4/17 15:24
 */
@Component
public class TestMiniProgramQrCodeService extends AlipayMiniProgramQrCodeService<
        TestCreateMiniProgramQrCodeRequest,
        TestMiniProgramQrCodeAuthenticationServiceOption> {
    public TestMiniProgramQrCodeService(@NonNull CacheClient client, @NonNull TestMiniProgramQrCodeAuthenticationServiceOption option) {
        super(client, option);
    }

    @Override
    protected Principal getPrincipalFromRequest(@NonNull HttpServletRequest request) throws Exception {
        return null;
    }
}
