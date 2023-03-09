package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.wechat.enterprise.client.EnterpriseWeChatClient;
import org.springframework.security.core.AuthenticationException;

import javax.validation.Validator;

/**
 * @author ZhangHan
 * @date 2022/11/8 20:49
 */
@AuthenticationType(value = "wechat.enterprise")
public class EnterpriseWeChatAuthenticationService extends OAuth2AuthenticationServiceBase {
    private final EnterpriseWeChatClient weChatClient;

    public EnterpriseWeChatAuthenticationService(Validator validator, OAuth2StateService stateService, EnterpriseWeChatClient weChatClient) {
        super(validator, stateService);
        this.weChatClient = weChatClient;
    }

    @Override
    protected BasicPrincipal loadSnsUser(String code) throws AuthenticationException {
        //执行企业微信对接程序
        String userId = weChatClient.getUserId(code);
        return null;
    }
}
