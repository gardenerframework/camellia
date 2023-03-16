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
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

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
    protected String generateCode(@NonNull CreateWeChatMiniProgramQrCodeRequest request) {
        //微信要求32个字符以内，所以就简单一些
        return UUID.randomUUID().toString().substring(0, 32);
    }

    @Override
    protected String createImage(@NonNull CreateWeChatMiniProgramQrCodeRequest request, @NonNull String code) throws Exception {
        if (accessToken == null || accessToken.isExpired()) {
            obtainAccessToken();
        }
        //如果成功是直接获取到图片
        ResponseEntity<byte[]> response = restTemplate.postForEntity(
                "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token={token}",
                new GetUnlimitedQRCodeRequest(
                        getOption().getLandingPageUrl(),
                        code,
                        request.getSize()
                ),
                byte[].class,
                accessToken.getAccessToken()
        );
        //不是图片就是失败
        if (!MediaType.IMAGE_JPEG.equals(response.getHeaders().getContentType())) {
            throw new RuntimeException("response = " + new String(Objects.requireNonNull(response.getBody())));
        }
        return String.format("data:image/jpeg;base64,%s",
                Base64.getEncoder()
                        .encodeToString(Objects.requireNonNull(response.getBody()))
        );
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
    private static class GetUnlimitedQRCodeRequest {
        @Nullable
        private final String page;
        @NonNull
        private final String scene;
        private final int width;

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
