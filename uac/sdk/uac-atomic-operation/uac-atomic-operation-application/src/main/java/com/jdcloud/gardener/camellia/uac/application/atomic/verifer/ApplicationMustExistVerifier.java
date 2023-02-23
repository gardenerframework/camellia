package com.jdcloud.gardener.camellia.uac.application.atomic.verifer;

import com.jdcloud.gardener.camellia.uac.application.exception.client.ApplicationNotFoundException;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.BasicEntityExistenceChecker;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/11/7 13:46
 */
@SuperBuilder
public class ApplicationMustExistVerifier extends BasicEntityExistenceChecker<String, ApplicationEntityTemplate> {
    @Override
    protected void init() {
        super.init();
        this.setExceptionFactory((ids, reason) -> new ApplicationNotFoundException(String.join(",", ids)));
        this.setBasicLogTemplate(GenericLoggerStaticAccessor.basicLogger()::debug);
    }
}
