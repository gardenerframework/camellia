package com.jdcloud.gardener.camellia.authorization.cas.configuration;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.CasExceptions;
import com.jdcloud.gardener.camellia.authorization.cas.CasPackage;
import com.jdcloud.gardener.fragrans.api.standard.error.configuration.RevealError;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2022/8/23 7:04 下午
 */
@Configuration
@ComponentScan(basePackageClasses = CasPackage.class)
@RevealError(superClasses = {
        CasExceptions.ClientSideException.class,
        CasExceptions.ClientSideException.class,
})
public class CasConfiguration {
}
