package io.gardenerframework.camellia.authentication.server.main.utils;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * 用户认证服务的名册
 *
 * @author zhanghan30
 * @date 2023/3/6 12:42
 */
public interface UserAuthenticationServiceRegistry {
    /**
     * 获取已经注册的服务类型清单
     *
     * @return 清单
     */
    default Collection<String> getUserAuthenticationServiceTypes() {
        return getUserAuthenticationServiceTypes(true);
    }

    /**
     * 获取已经注册的服务类型清单
     *
     * @param ignorePreserved 是否保留
     * @return 清单
     */
    Collection<String> getUserAuthenticationServiceTypes(boolean ignorePreserved);

    /**
     * 是否包含当前服务
     *
     * @param type 类型
     * @return 是否包含
     */
    default boolean hasUserAuthenticationService(@NonNull String type) {
        return hasUserAuthenticationService(type, true);
    }

    /**
     * 是否包含类型
     *
     * @param type            类型
     * @param ignorePreserved 是否忽略被引擎预留的服务
     * @return 是否包含
     */
    boolean hasUserAuthenticationService(@NonNull String type, boolean ignorePreserved);

    /**
     * 获取用户认证服务
     *
     * @param type 类型
     * @return 服务
     */
    @Nullable
    default UserAuthenticationService getUserAuthenticationService(@NonNull String type) {
        return getUserAuthenticationService(type, true);
    }

    /**
     * 获取用户认证服务
     *
     * @param type            类型
     * @param ignorePreserved 忽略引擎保留的服务
     * @return 服务
     */
    @Nullable
    UserAuthenticationService getUserAuthenticationService(@NonNull String type, boolean ignorePreserved);
}
