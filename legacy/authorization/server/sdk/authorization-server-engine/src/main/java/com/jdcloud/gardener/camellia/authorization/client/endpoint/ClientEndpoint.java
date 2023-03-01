package com.jdcloud.gardener.camellia.authorization.client.endpoint;

import com.jdcloud.gardener.camellia.authorization.client.schema.response.ClientAppearance;
import com.jdcloud.gardener.camellia.authorization.common.api.group.AuthorizationServerRestController;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.client.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Component
@AuthorizationServerRestController
@RequestMapping("/client")
@Slf4j
@AllArgsConstructor
public class ClientEndpoint {
    private final RegisteredClientRepository repository;
    private final Converter<? extends RegisteredClient, ? extends ClientAppearance> clientToVoConverter;

    @GetMapping("/{clientId}")
    public ClientAppearance readClientInfo(@Valid @NotBlank @PathVariable("clientId") String clientId) {
        RegisteredClient client = repository.findByClientId(clientId);
        if (client != null) {
            return castConverter().convert(client);
        } else {
            throw new BadRequestException("client " + clientId + " was not found");
        }
    }

    @SuppressWarnings("unchecked")
    private Converter<RegisteredClient, ClientAppearance> castConverter() {
        return (Converter<RegisteredClient, ClientAppearance>) this.clientToVoConverter;
    }
}
