package com.jdcloud.gardener.camellia.uac.client.atomic.verifier;

import com.jdcloud.gardener.camellia.uac.client.exception.client.ClientAlreadyExistedException;
import com.jdcloud.gardener.camellia.uac.client.schema.entity.ClientEntityTemplate;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.BasicChecker;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.BasicEntityRecordIdExtractor;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import com.jdcloud.gardener.fragrans.log.common.schema.reason.AlreadyExisted;
import com.jdcloud.gardener.fragrans.log.schema.word.Word;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

/**
 * @author ZhangHan
 * @date 2022/11/8 13:53
 */
@SuperBuilder
public class ClientMustNotExistedVerifier extends BasicChecker<String, ClientEntityTemplate> implements BasicEntityRecordIdExtractor<String, ClientEntityTemplate> {

    @Override
    protected Word getLogHow() {
        return new AlreadyExisted();
    }

    @Override
    protected void init() {
        super.init();
        this.setBasicLogTemplate(GenericLoggerStaticAccessor.basicLogger()::debug);
        //非空结果会失败
        this.setFailOnNonEmptyRecordCollection(true);
        //空结果不会
        this.setFailOnEmptyRecordCollection(false);
        this.setExceptionFactory((ids, reason) -> new ClientAlreadyExistedException(String.join(",", ids)));
    }

    @Override
    protected boolean doCheck(@Nullable ClientEntityTemplate record) {
        return record != null;
    }
}
