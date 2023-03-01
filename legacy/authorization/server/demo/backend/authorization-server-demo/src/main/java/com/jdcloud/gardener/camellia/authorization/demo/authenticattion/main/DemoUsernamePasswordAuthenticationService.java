package com.jdcloud.gardener.camellia.authorization.demo.authenticattion.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.UsernamePasswordAuthenticationServiceBase;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.BasicCredentials;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author ZhangHan
 * @date 2022/5/15 0:34
 */
@Component
@ConditionalOnClass(UsernamePasswordAuthenticationServiceBase.class)
public class DemoUsernamePasswordAuthenticationService extends UsernamePasswordAuthenticationServiceBase {

    @Override
    protected boolean compareCredentials(BasicCredentials request, BasicCredentials authority) {
        return Objects.equals(request, authority);
    }
}
