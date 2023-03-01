package com.jdcloud.gardener.camellia.uac.common.configuration;

import com.jdcloud.gardener.camellia.uac.common.atomic.PasswordEncoder;
import com.jdcloud.gardener.camellia.uac.common.atomic.support.EntityIdSaltPasswordEncoder;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2022/11/14 12:36
 */
@Configuration
@Import(UacAtomicOperationConfiguration.UacAtomicOperationDefaultConfiguration.class)
public class UacAtomicOperationConfiguration {
    @Configuration
    public static class UacAtomicOperationDefaultConfiguration {
        @Bean
        @ConditionalOnMissingBean(PasswordEncoder.class)
        public PasswordEncoder<BasicEntity<String>> defaultPasswordEncoder() {
            return new EntityIdSaltPasswordEncoder<>();
        }
    }
}
