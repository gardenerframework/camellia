package com.jdcloud.gardener.camellia.uac.test.accout.extend;

import com.jdcloud.gardener.camellia.uac.account.dao.mapper.AccountMapperTemplate;
import com.jdcloud.gardener.fragrans.data.persistence.annotation.OverrideSqlProviderAnnotation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.Primary;

/**
 * @author zhanghan30
 * @date 2022/9/20 18:04
 */
@Mapper
@Primary
@OverrideSqlProviderAnnotation(ExtendAccountSqlBuilder.class)
public interface ExtendAccountMapper extends AccountMapperTemplate<ExtendAccount, ExtendCriteria> {
}
