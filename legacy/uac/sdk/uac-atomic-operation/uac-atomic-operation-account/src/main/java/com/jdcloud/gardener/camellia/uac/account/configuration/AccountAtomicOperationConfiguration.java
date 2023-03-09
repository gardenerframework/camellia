package com.jdcloud.gardener.camellia.uac.account.configuration;

import com.jdcloud.gardener.camellia.uac.account.defaults.atomic.DefaultAccountAtomicOperation;
import com.jdcloud.gardener.camellia.uac.account.defaults.atomic.DefaultPrincipalQueryCriteriaFactory;
import com.jdcloud.gardener.camellia.uac.account.defaults.dao.mapper.DefaultAccountMapperPackage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 默认的mapper和原子操作的配置
 *
 * @author zhanghan30
 * @date 2022/9/20 16:13
 */
@Configuration
@Import({
        AccountAtomicOperationConfiguration.AccountAtomicOperationDefaultsConfiguration.class
})
public class AccountAtomicOperationConfiguration {

    /**
     * 去完成默认的设置
     */
    @Configuration
    @MapperScan(basePackageClasses = {
            DefaultAccountMapperPackage.class
    })
    @Import({
            DefaultAccountAtomicOperation.class,
            DefaultPrincipalQueryCriteriaFactory.class
    })
    public static class AccountAtomicOperationDefaultsConfiguration {

    }
}
