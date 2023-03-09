package com.jdcloud.gardener.camellia.authorization.test.utils;

import com.jdcloud.gardener.camellia.authorization.authentication.main.UsernamePasswordAuthenticationServiceBase;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.BasicCredentials;
import org.springframework.stereotype.Component;

/**
 * @author ZhangHan
 * @date 2022/5/14 21:37
 */
@Component
public class SimpleUsernamePasswordAuthenticationService extends UsernamePasswordAuthenticationServiceBase {
    @Override
    protected boolean compareCredentials(BasicCredentials request, BasicCredentials authority) {
        return true;
    }
}
