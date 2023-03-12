package io.gardenerframework.camellia.authentication.infra.sms.challenge.configuration;

import io.gardenerframework.camellia.authentication.infra.sms.challenge.JdCloudSmsClientPackage;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.client.JdCloudSmsClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author zhanghan30
 * @date 2023/2/16 11:53
 */
@Configuration
@ComponentScan(
        basePackageClasses = JdCloudSmsClientPackage.class,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = {
                                JdCloudSmsClientSecurityOption.class,
                                JdCloudSmsClient.class
                        }
                )
        }
)
public class JdCloudSmsClientConfiguration {
}
