package io.gardenerframework.camellia.authentication.server.main.management.schema.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2023/2/24 16:09
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAvailableAuthenticationTypesResponse {
    private Collection<String> types;
}
