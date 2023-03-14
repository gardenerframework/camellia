package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.server.configuration.LdapUserServiceOption;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.PasswordCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.UserService;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/14 11:58
 */
@RequiredArgsConstructor
public class LdapUserService implements UserService, InitializingBean {
    @NonNull
    private final LdapUserServiceOption option;
    @NonNull
    private LdapTemplate ldapTemplate;

    @Override
    public User authenticate(
            @NonNull Principal principal,
            @NonNull PasswordCredentials credentials,
            Map<String, Object> context
    ) throws AuthenticationException {
        return null;
    }

    @Override
    public User load(
            @NonNull Principal principal,
            Map<String, Object> context
    ) throws AuthenticationException,
            UnsupportedOperationException {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(option.getUrl());
        contextSource.setPassword(option.getPassword());
        contextSource.setUserDn(option.getUserDn());
        contextSource.setBase(option.getBaseDomainDn());
        this.ldapTemplate = new LdapTemplate(contextSource);
    }
}
