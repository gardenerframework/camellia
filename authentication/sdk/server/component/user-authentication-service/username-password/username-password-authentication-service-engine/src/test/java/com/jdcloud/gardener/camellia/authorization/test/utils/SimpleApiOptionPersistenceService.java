package com.jdcloud.gardener.camellia.authorization.test.utils;

import com.jdcloud.gardener.fragrans.api.options.persistence.ApiOptionPersistenceService;
import com.jdcloud.gardener.fragrans.api.options.persistence.exception.ApiOptionPersistenceException;
import com.jdcloud.gardener.fragrans.api.options.persistence.schema.ApiOptionRecordSkeleton;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author ZhangHan
 * @date 2022/5/14 21:36
 */
@Component
public class SimpleApiOptionPersistenceService implements ApiOptionPersistenceService {
    @Nullable
    @Override
    public ApiOptionRecordSkeleton readOption(String id) throws ApiOptionPersistenceException {
        return null;
    }

    @Override
    public String saveOption(String id, Object option) throws ApiOptionPersistenceException {
        return null;
    }
}
