package com.jdcloud.gardener.camellia.uac.application.atomic.verifer;

import com.jdcloud.gardener.camellia.uac.application.exception.client.ApplicationDisabledException;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.BasicEntityEnabledStatusChecker;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/11/7 19:00
 */
@SuperBuilder
public class ApplicationMustBeEnabledVerifier extends BasicEntityEnabledStatusChecker<String, ApplicationEntityTemplate> {
    @Override
    protected void init() {
        super.init();
        this.setExceptionFactory((ids, reason) -> new ApplicationDisabledException(String.join(",", ids)));
        this.setBasicLogTemplate(GenericLoggerStaticAccessor.basicLogger()::debug);
    }
}
