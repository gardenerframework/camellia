package com.jdcloud.gardener.camellia.authorization.authentication.configuration;

import com.jdcloud.gardener.fragrans.api.options.schema.ApiOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Positive;

/**
 * @author zhanghan30
 * @date 2022/1/7 12:28 下午
 */
@ApiOption(readonly = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class OAuth2AuthorizationConsentOption {
    /**
     * 在批准页面多长时间不点，state过期
     */
    @Positive
    private long consentStateTtl = 120L;
    /**
     * 授权批准的有效期
     * <p>
     * 默认2小时
     * <p>
     * 也就是2小时后要重新批准
     */
    @Positive
    private long consentTll = 7200L;
}
