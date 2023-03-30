package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.response;

import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2023/3/29 13:24
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListAuthenticatorsResponse {
    @NonNull
    private Collection<String> authenticators = new ArrayList<>();
}
