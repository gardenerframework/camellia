package com.jdcloud.gardener.camellia.authorization.test;

import com.jdcloud.gardener.camellia.authorization.qrcode.service.PrincipalProvider;
import com.jdcloud.gardener.fragrans.api.options.persistence.ApiOptionPersistenceService;
import com.jdcloud.gardener.fragrans.api.options.persistence.exception.ApiOptionPersistenceException;
import com.jdcloud.gardener.fragrans.api.options.persistence.schema.ApiOptionRecordSkeleton;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/4/11 5:16 下午
 */
@SpringBootApplication
public class QrCodeTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(QrCodeTestApplication.class, args);
    }

    @Bean
    public ApiOptionPersistenceService apiOptionPersistenceService() {
        return new ApiOptionPersistenceService() {
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
    public PrincipalProvider qrCodeUserEssentialsProvider() {
        return new PrincipalProvider() {

            @Override
            public Object apply(Object o) {
                return null;
            }
        };
    }
}
