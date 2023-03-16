package io.gardenerframework.camellia.authentication.server.main.schema.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhanghan30
 * @date 2023/3/16 16:03
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetQrCodeStateResponse {
    private String state;
}
