package com.jdcloud.gardener.camellia.uac.application.connector.rbac.endpoint.validation;

/**
 * @author zhanghan30
 * @date 2022/11/18 13:43
 */
public interface ApplicationIdChecker {
    /**
     * 检查应用id
     *
     * @param applicationId 应用id
     * @return 是否合法
     */
    boolean check(String applicationId);
}
