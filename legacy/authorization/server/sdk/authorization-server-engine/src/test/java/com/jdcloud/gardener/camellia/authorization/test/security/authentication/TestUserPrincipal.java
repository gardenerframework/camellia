package com.jdcloud.gardener.camellia.authorization.test.security.authentication;


import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.BasicCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Date;

/**
 * @author ZhangHan
 * @date 2022/4/23 11:38
 */
@AllArgsConstructor
public class TestUserPrincipal extends User {
    @Getter
    @Setter
    private String username;

    public TestUserPrincipal(String id, BasicCredentials credentials, Collection<BasicPrincipal> principals, boolean locked, boolean enabled, @Nullable Date passwordExpiresAt, @Nullable Date subjectExpiresAt, String username) {
        super(id, credentials, principals, locked, enabled, passwordExpiresAt, subjectExpiresAt, username, null);
        this.username = username;
    }

    @Override
    public String getId() {
        return username;
    }
}
