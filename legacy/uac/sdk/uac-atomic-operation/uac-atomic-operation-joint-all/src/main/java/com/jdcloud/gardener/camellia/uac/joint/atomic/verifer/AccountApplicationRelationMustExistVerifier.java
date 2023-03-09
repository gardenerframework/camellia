package com.jdcloud.gardener.camellia.uac.joint.atomic.verifer;

import com.jdcloud.gardener.camellia.uac.joint.schema.entity.AccountApplicationRelation;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.RecordChecker;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/11/7 12:13
 */
public class AccountApplicationRelationMustExistVerifier implements RecordChecker<AccountApplicationRelation> {
    @Override
    public void check(@Nullable AccountApplicationRelation record) {
        if (record == null) {

        }
    }
}
