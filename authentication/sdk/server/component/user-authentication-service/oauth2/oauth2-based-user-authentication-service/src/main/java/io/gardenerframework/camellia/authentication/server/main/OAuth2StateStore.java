package io.gardenerframework.camellia.authentication.server.main;

import lombok.NonNull;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/3/6 18:04
 */
public interface OAuth2StateStore {
    /**
     * 生成一个state
     *
     * @param service 那个服务
     * @param state   state
     * @param ttl     有效期
     * @throws Exception 发生的问题
     */
    void save(@NonNull Class<? extends OAuth2BasedUserAuthenticationService> service, @NonNull String state, Duration ttl) throws Exception;

    /**
     * 检查state是否合法
     *
     * @param service 服务
     * @param state   state
     * @return 是否有效 - 如果有效则应当直接删除state
     * @throws Exception 发生的问题
     */
    boolean verify(@NonNull Class<? extends OAuth2BasedUserAuthenticationService> service, @NonNull String state) throws Exception;
}
