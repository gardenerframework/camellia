package com.jdcloud.gardener.camellia.uac.client.atomic.verifier;

import com.jdcloud.gardener.camellia.uac.client.exception.client.ClientNotFoundException;
import com.jdcloud.gardener.camellia.uac.client.schema.entity.ClientEntityTemplate;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.BasicEntityExistenceChecker;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/9/21 13:24
 */
@SuperBuilder
public class CLintMustExistVerifier extends BasicEntityExistenceChecker<String, ClientEntityTemplate> {
    @Override
    protected void init() {
        super.init();
        this.setExceptionFactory((ids, reason) -> new ClientNotFoundException(String.join(",", ids)));
        this.setBasicLogTemplate(GenericLoggerStaticAccessor.basicLogger()::debug);
    }
}
