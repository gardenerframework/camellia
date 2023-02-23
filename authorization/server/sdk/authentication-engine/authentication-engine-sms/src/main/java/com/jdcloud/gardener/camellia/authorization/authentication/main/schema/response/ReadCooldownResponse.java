package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhanghan30
 * @date 2022/4/25 7:44 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadCooldownResponse {
    private long cooldown;
}
