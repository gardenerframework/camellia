package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.credentials;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.BasicCredentials;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhanghan30
 * @date 2022/5/12 9:26 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class MfaResponseCredentials extends BasicCredentials {
    private final String response;
}
