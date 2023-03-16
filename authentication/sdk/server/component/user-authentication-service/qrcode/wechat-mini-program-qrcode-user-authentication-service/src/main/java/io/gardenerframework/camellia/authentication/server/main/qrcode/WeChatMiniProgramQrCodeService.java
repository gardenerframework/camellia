package io.gardenerframework.camellia.authentication.server.main.qrcode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.server.configuration.WeChatMiniProgramQrCodeAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.configuration.WeChatMiniProgramQrCodeServiceComponent;
import io.gardenerframework.camellia.authentication.server.main.schema.request.CreateWeChatMiniProgramQrCodeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/16 18:34
 */
@WeChatMiniProgramQrCodeServiceComponent
public class WeChatMiniProgramQrCodeService extends QrCodeService<
        CreateWeChatMiniProgramQrCodeRequest,
        WeChatMiniProgramQrCodeAuthenticationServiceOption
        > implements InitializingBean {
    private final RestTemplate restTemplate = new RestTemplate();
    private AccessToken accessToken;

    public WeChatMiniProgramQrCodeService(@NonNull WeChatMiniProgramQrCodeAuthenticationServiceOption option, @NonNull CacheClient client) {
        super(option, client);
    }

    @Override
    protected String createImage(@NonNull CreateWeChatMiniProgramQrCodeRequest request, @NonNull String code) throws Exception {
        if (accessToken == null || accessToken.isExpired()) {
            obtainAccessToken();
        }
        Map<String, Object> response = restTemplate.getForObject(
                "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token={token}" +
                        "&scene={scene}&page={page}&width={width}",
                Map.class,
                accessToken.getAccessToken(),
                code,
                getOption().getLandingPageUrl(),
                request.getSize()
        );
        if (response == null || response.get("buffer") == null) {
            throw new RuntimeException("response = " + new ObjectMapper().writeValueAsString(response));
        }
        return (String) response.get("buffer");
    }

    @Override
    protected Principal getPrincipalFromRequest(@NonNull HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        //fix 微信坑逼 text/plain
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
        restTemplate.getMessageConverters().add(converter);
        obtainAccessToken();
    }

    private void obtainAccessToken() {
        Map<String, Object> response = restTemplate.getForObject(
                "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appid}&secret={secret}",
                Map.class,
                getOption().getAppId(),
                getOption().getAppSecret()
        );
        if (response.get("access_token") == null) {
            try {
                throw new IllegalStateException("error = " + new ObjectMapper().writeValueAsString(response));
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(e);
            }
        }
        accessToken = new AccessToken(
                (String) response.get("access_token"),
                Date.from(Instant.now().plus(Duration.ofSeconds((Integer) response.get("expires_in"))))
        );
    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static class AccessToken {
        @NonNull
        private String accessToken;
        @NonNull
        private Date expiryTime;

        public boolean isExpired() {
            return new Date().after(this.expiryTime);
        }
    }
}
