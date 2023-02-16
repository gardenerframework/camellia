package io.gardenerframework.camellia.authentication.server.main.client;

import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * 用于给出当前安全环境下的应用组
 * <p>
 * 开发人员基于client id给出当前应用的应用应该是什么
 * <p>
 * 需要注意的是，一个应用有多个client id是正常现象
 *
 * @author zhanghan30
 * @date 2022/4/25 6:51 下午
 */
@FunctionalInterface
public interface ClientMetadataProvider {
    /**
     * 返回元数据的部分，多个provider完全提供完所有碎片后整合为最终的元数据
     *
     * @param clientId 客户端id，如果为空代表当前认证过程没有任何客户端信息，比如用户只是打开登录页面进行登录，而不是第三方请求了IAM
     * @return 元数据
     */
    Map<String, String> getMetadataPiece(@Nullable String clientId);
}
