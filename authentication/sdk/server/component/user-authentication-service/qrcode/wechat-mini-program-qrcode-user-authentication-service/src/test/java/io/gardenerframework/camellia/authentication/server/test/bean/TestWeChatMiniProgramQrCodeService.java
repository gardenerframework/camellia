package io.gardenerframework.camellia.authentication.server.test.bean;

import io.gardenerframework.camellia.authentication.server.main.qrcode.WeChatMiniProgramQrCodeService;
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
public class TestWeChatMiniProgramQrCodeService extends WeChatMiniProgramQrCodeService<
        TestCreateWeChatMiniProgramQrCodeRequest,
        TestWeChatMiniProgramQrCodeAuthenticationServiceOption> {
    public TestWeChatMiniProgramQrCodeService(@NonNull CacheClient client, @NonNull TestWeChatMiniProgramQrCodeAuthenticationServiceOption option) {
        super(client, option);
    }

    @Override
    protected Principal getPrincipalFromRequest(@NonNull HttpServletRequest request) throws Exception {
        return null;
    }
}
