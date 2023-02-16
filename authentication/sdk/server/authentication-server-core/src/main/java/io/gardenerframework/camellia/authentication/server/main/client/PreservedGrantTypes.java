package io.gardenerframework.camellia.authentication.server.main.client;

/**
 * 工程预留的授权类型
 * <p>
 * 也是认证过程主要用的
 * <p>
 * 其它过程用的自行定义
 *
 * @author zhanghan30
 * @date 2022/5/12 1:29 下午
 */
public interface PreservedGrantTypes {
    /**
     * 授权类型为认证用户
     */
    String USER_AUTHENTICATION = "user_authentication";
}
