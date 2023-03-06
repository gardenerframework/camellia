package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;

/**
 * @author zhanghan30
 * @date 2023/3/6 12:04
 */
public interface OAuth2BasedIamUserReader {
    /**
     * 向oauth2的iam读取用户
     *
     * @param code 包含code和state
     * @return 读取出来的用户，如果用户不存在则抛出异常
     * @throws Exception 发生的问题，如果是{@link AuthenticationException}则会被支持抛出
     */
    @Nullable
    Principal readUser(@NonNull String code)
            throws Exception;
}
