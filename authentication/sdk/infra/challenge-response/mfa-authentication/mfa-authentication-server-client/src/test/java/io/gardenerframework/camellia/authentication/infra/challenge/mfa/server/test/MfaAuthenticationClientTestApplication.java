package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/30 14:08
 */
@SpringBootApplication
public class MfaAuthenticationClientTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfaAuthenticationClientTestApplication.class, args);
    }

    @Bean
    public DiscoveryClient discoveryClient() {
        return new DiscoveryClient() {
            @Override
            public String description() {
                return null;
            }

            @Override
            public List<ServiceInstance> getInstances(String serviceId) {
                return Collections.singletonList(new ServiceInstance() {
                    @Override
                    public String getServiceId() {
                        return null;
                    }

                    @Override
                    public String getHost() {
                        return "localhost";
                    }

                    @Override
                    public int getPort() {
                        return 18888;
                    }

                    @Override
                    public boolean isSecure() {
                        return false;
                    }

                    @Override
                    public URI getUri() {
                        return null;
                    }

                    @Override
                    public Map<String, String> getMetadata() {
                        return null;
                    }
                });
            }

            @Override
            public List<String> getServices() {
                return null;
            }
        };
    }
}
