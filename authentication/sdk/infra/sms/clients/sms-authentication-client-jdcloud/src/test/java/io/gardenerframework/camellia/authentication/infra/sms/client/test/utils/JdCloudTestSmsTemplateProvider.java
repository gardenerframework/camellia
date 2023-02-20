package io.gardenerframework.camellia.authentication.infra.sms.client.test.utils;

import io.gardenerframework.camellia.authentication.infra.common.Scenario;
import io.gardenerframework.camellia.authentication.infra.sms.client.jdcloud.JdCloudSmsTemplateProvider;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2023/2/16 11:47
 */
@Component
public class JdCloudTestSmsTemplateProvider implements JdCloudSmsTemplateProvider {
    @Override
    public String getSignId(String applicationId, Class<? extends Scenario> scenario) {
        return "qm_f58d5574f1fb43a0b04333b61bb74c14";
    }

    @Override
    public String getTemplateId(String applicationId, Class<? extends Scenario> scenario) {
        return "mb_47175bac722446ba8eb81819bc3a49a1";
    }
}
