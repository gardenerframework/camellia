package io.gardenerframework.camellia.authentication.server.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.server.client.schema.response.ClientAppearance;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.camellia.authentication.server.user.schema.response.UserAppearance;
import io.gardenerframework.fragrans.api.options.persistence.ApiOptionPersistenceService;
import io.gardenerframework.fragrans.api.options.persistence.exception.ApiOptionPersistenceException;
import io.gardenerframework.fragrans.api.options.persistence.schema.ApiOptionRecordSkeleton;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2021/12/20 2:38 下午
 */
@SpringBootApplication
public class AuthorizationServerEngineTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerEngineTestApplication.class, args);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(
                new HttpComponentsClientHttpRequestFactory()
        );
        restTemplate.setInterceptors(
                Collections.singletonList(new BasicAuthenticationInterceptor("test", "123"))
        );
        return restTemplate;
    }

    @Bean
    public ApiOptionPersistenceService<ApiOptionRecordSkeleton> apiOptionPersistenceService() {
        return new ApiOptionPersistenceService<ApiOptionRecordSkeleton>() {
            @Nullable
            @Override
            public ApiOptionRecordSkeleton readOption(String id) throws ApiOptionPersistenceException {
                return null;
            }

            @Override
            public String saveOption(String id, Object option) throws ApiOptionPersistenceException {
                return null;
            }
        };
    }

    @Bean
    public Converter<User, UserAppearance> userAppearanceConverter(ObjectMapper mapper) {
        return new Converter<User, UserAppearance>() {
            @Nullable
            @Override
            public UserAppearance convert(User source) {
                return mapper.convertValue(source, UserAppearance.class);
            }
        };
    }

    @Bean
    public Converter<RegisteredClient, ClientAppearance> clientAppearanceConverter(ObjectMapper mapper) {
        return new Converter<RegisteredClient, ClientAppearance>() {
            @Nullable
            @Override
            public ClientAppearance convert(RegisteredClient source) {
                return mapper.convertValue(source, ClientAppearance.class);
            }
        };
    }
}
