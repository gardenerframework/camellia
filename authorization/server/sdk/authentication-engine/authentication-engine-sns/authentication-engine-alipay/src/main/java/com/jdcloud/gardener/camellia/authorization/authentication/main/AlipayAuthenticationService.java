package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.alipay.easysdk.base.oauth.Client;
import com.alipay.easysdk.base.oauth.models.AlipaySystemOauthTokenResponse;
import com.alipay.easysdk.factory.Factory;
import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.AlipayOpenIdPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

import javax.validation.Validator;

/**
 * @author zhanghan30
 * @date 2022/11/10 15:17
 */
@AuthenticationType("alipay")
public class AlipayAuthenticationService extends OAuth2AuthenticationServiceBase {

    public AlipayAuthenticationService(Validator validator, OAuth2StateService stateService) {
        super(validator, stateService);
    }

    @Override
    protected BasicPrincipal loadSnsUser(String code) throws AuthenticationException {
        Client client = Factory.getClient(Client.class);
        Assert.notNull(client, "cannot get alipay oauth2 client");
        try {
            AlipaySystemOauthTokenResponse token = client.getToken(code);
            return new AlipayOpenIdPrincipal(token.getUserId());
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }
}
