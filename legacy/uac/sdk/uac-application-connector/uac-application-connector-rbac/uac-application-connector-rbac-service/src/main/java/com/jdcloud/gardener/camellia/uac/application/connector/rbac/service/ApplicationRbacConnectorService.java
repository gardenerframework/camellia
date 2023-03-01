package com.jdcloud.gardener.camellia.uac.application.connector.rbac.service;

import com.jdcloud.gardener.camellia.uac.application.connector.rbac.skeleton.ApplicationRbacConnectorSkeleton;

import java.util.Collection;

/**
 * 标记为服务类
 *
 * @author zhanghan30
 * @date 2022/11/17 20:08
 */
public interface ApplicationRbacConnectorService extends ApplicationRbacConnectorSkeleton {
    /**
     * 返回支持的应用id清单，为空会抛异常(什么都不支持有个屁用)
     *
     * @return 支持的应用id清单
     */
    Collection<String> supportApplicationIds();
}
