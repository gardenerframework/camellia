package com.jdcloud.gardener.camellia.authorization.test.security.authentication;


import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author ZhangHan
 * @date 2022/4/23 11:38
 */
@SuperBuilder
public class TestUserPrincipal extends User {
    @Getter
    @Setter
    private String username;

    @Override
    public String getId() {
        return username;
    }
}
