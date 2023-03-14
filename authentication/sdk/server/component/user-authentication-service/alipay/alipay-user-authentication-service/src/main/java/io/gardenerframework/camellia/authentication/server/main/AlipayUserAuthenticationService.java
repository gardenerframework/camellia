package io.gardenerframework.camellia.authentication.server.main;

import com.alipay.easysdk.base.oauth.Client;
import com.alipay.easysdk.base.oauth.models.AlipaySystemOauthTokenResponse;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import io.gardenerframework.camellia.authentication.server.configuration.AlipayUserAuthenticationServiceComponent;
import io.gardenerframework.camellia.authentication.server.configuration.AlipayUserAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.AlipayOpenIdPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import javax.validation.Validator;

/**
 * @author zhanghan30
 * @date 2023/3/9 07:23
 */
@AuthenticationType("alipay")
@AlipayUserAuthenticationServiceComponent
public class AlipayUserAuthenticationService extends OAuth2BasedUserAuthenticationService {
    private String alipayOptionSign = "";

    @Setter(onMethod = @__(@Autowired), value = AccessLevel.PRIVATE)
    private AlipayUserAuthenticationServiceOption option;

    public AlipayUserAuthenticationService(@NonNull Validator validator, OAuth2StateStore oAuth2StateStore) {
        super(validator, oAuth2StateStore);
    }

    @Nullable
    @Override
    protected Principal getPrincipal(@NonNull String authorizationCode) throws Exception {
        //初始化阿里客户端
        initAlipayClientFactory();
        Client client = Factory.getClient(Client.class);
        AlipaySystemOauthTokenResponse token = client.getToken(authorizationCode);
        return AlipayOpenIdPrincipal.builder().name(token.getUserId()).build();
    }

    private boolean signUpdated() {
        String sign = String.format("%s:%s:%s:%s",
                option.getAppId(),
                option.getPrivateKey(),
                option.getEncryptKey(),
                option.getAliPublicKey()
        );
        if (alipayOptionSign.equals(sign)) {
            return false;
        } else {
            alipayOptionSign = sign;
            return true;
        }
    }


    private synchronized void initAlipayClientFactory() {
        if (signUpdated()) {
            Config config = new Config();
            config.protocol = "https";
            config.gatewayHost = "openapi.alipay.com";
            config.signType = "RSA2";
            config.appId = option.getAppId();
            config.merchantPrivateKey = option.getPrivateKey();
            config.encryptKey = option.getEncryptKey();
            config.alipayPublicKey = option.getAliPublicKey();
            Factory.setOptions(config);
        }
    }
}
