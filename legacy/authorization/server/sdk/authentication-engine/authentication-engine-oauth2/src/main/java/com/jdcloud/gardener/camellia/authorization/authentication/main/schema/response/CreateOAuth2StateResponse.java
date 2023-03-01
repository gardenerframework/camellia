package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhanghan30
 * @date 2022/11/9 16:14
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateOAuth2StateResponse {
    private String state;
}
