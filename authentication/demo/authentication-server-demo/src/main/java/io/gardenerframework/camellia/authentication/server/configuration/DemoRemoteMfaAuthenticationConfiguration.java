package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.main.mfa.GenericMfaAuthenticatorClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@EnableFeignClients(clients = GenericMfaAuthenticatorClient.class)
public class DemoRemoteMfaAuthenticationConfiguration {
    @Bean
    public DiscoveryClient mfaAuthenticationServerDiscoveryClient() {
        return new DiscoveryClient() {
            @Override
            public String description() {
                return null;
            }

            @Override
            public List<ServiceInstance> getInstances(String serviceId) {
                return Collections.singletonList(
                        new ServiceInstance() {
                            @Override
                            public String getServiceId() {
                                return "demo-mfa-authentication-server";
                            }

                            @Override
                            public String getHost() {
                                return "localhost";
                            }

                            @Override
                            public int getPort() {
                                return 18089;
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
                        }
                );
            }

            @Override
            public List<String> getServices() {
                return Collections.singletonList("demo-mfa-authentication-server");
            }
        };
    }
}
