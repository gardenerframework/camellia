package com.jdcloud.gardener.camellia.authorization.demo.qrcode.service;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.UsernamePrincipal;
import com.jdcloud.gardener.camellia.authorization.qrcode.service.PrincipalProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhanghan30
 * @date 2022/8/9 7:00 下午
 */
@ConditionalOnClass(PrincipalProvider.class)
@Component
public class EmptyPrincipalProvider implements PrincipalProvider<UsernamePrincipal> {
    @Override
    public UsernamePrincipal apply(HttpServletRequest request) {
        return null;
    }
}
