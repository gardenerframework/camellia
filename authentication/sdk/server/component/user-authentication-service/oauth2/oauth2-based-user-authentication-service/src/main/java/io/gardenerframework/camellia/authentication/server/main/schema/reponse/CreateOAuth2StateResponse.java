package io.gardenerframework.camellia.authentication.server.main.schema.reponse;

import lombok.*;

/**
 * @author zhanghan30
 * @date 2022/11/9 16:14
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateOAuth2StateResponse {
    @NonNull
    private String state;
}
