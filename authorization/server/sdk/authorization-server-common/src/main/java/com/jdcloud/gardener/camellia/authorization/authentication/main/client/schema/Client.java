package com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema;

import com.jdcloud.gardener.camellia.authorization.common.Version;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Set;

/**
 * 客户端信息
 * <p>
 * 这个客户端信息是认证过程中用到的，仅限认证服务器内使用
 * <p>
 * 如果资源服务器想要获取客户端的详细信息，可以调用客户端管理服务器的接口
 * <p>
 * 原则上只提供客户端的id对外
 *
 * @author zhanghan30
 * @date 2022/4/19 9:22 下午
 */
@AllArgsConstructor
@Getter
@LogTarget("客户端")
public class Client implements Serializable {
    private static final long serialVersionUID = Version.current;
    /**
     * client id
     */
    private final String clientId;
    /**
     * 访问的授权类型
     */
    private final String grantType;
    /**
     * 对用户信息的访问范围
     */
    private final Set<String> scopes;
}
