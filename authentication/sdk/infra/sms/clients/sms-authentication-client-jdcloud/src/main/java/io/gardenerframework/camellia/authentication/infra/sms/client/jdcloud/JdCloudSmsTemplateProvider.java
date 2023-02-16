package io.gardenerframework.camellia.authentication.infra.sms.client.jdcloud;

import io.gardenerframework.camellia.authentication.infra.sms.core.Scenario;

/**
 * @author zhanghan30
 * @date 2023/2/16 11:34
 */
public interface JdCloudSmsTemplateProvider {
    /**
     * 返回签名id
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @return 签名id
     */
    String getSignId(String applicationId, Class<? extends Scenario> scenario);

    /**
     * 返回模板id
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @return 模板id
     */
    String getTemplateId(String applicationId, Class<? extends Scenario> scenario);
}
