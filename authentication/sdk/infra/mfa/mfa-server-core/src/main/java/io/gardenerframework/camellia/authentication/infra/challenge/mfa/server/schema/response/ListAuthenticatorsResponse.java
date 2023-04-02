package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2023/3/29 13:24
 */
@Getter
@Setter
@NoArgsConstructor
public class ListAuthenticatorsResponse {
    private Collection<String> authenticators = new ArrayList<>();

    public ListAuthenticatorsResponse(Collection<String> authenticators) {
        setAuthenticators(authenticators);
    }

    public void setAuthenticators(Collection<String> authenticators) {
        this.authenticators = authenticators == null ? new ArrayList<>() : authenticators;
    }
}
