package com.jdcloud.gardener.camellia.uac.common.configuration;

import com.jdcloud.gardener.camellia.uac.common.UacApiEngineCommonPackage;
import com.jdcloud.gardener.camellia.uac.common.exception.UacExceptionBase;
import com.jdcloud.gardener.fragrans.api.standard.error.configuration.RevealError;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2022/8/17 9:23 下午
 */
@Configuration
@ComponentScan(basePackageClasses = UacApiEngineCommonPackage.class)
@RevealError(superClasses = UacExceptionBase.class)
public class UacApiEngineCommonConfiguration {
}
