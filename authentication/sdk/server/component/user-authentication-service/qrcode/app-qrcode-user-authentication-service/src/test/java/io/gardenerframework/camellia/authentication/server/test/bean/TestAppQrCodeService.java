package io.gardenerframework.camellia.authentication.server.test.bean;

import io.gardenerframework.camellia.authentication.server.main.qrcode.AppQrCodeService;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.test.conf.TestAppQrCodeAuthenticationServiceOption;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhanghan30
 * @date 2023/3/20 15:40
 */
@Component
public class TestAppQrCodeService extends AppQrCodeService<TestCreateQrCodeRequest, TestAppQrCodeAuthenticationServiceOption> {
    @Override
    protected String buildPageFinalUrl(@NonNull String code) throws Exception {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOption().getPageUrl());
        builder.queryParam("code", code);
        return builder.build().toString();
    }

    protected TestAppQrCodeService(@NonNull CacheClient client, @NonNull TestAppQrCodeAuthenticationServiceOption option) {
        super(client, option);
    }

    @Override
    protected Principal getPrincipalFromRequest(@NonNull HttpServletRequest request) throws Exception {
        return null;
    }
}
