package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * @author zhanghan30
 * @date 2023/3/16 18:23
 */
@ApiOption(readonly = false)
@Getter
@Setter
@NoArgsConstructor
public abstract class AppQrCodeAuthenticationServiceOption {
    /**
     * 落地页地址
     */
    @Nullable
    private String pageUrl;
    /**
     * 存活时间
     */
    @Positive
    long ttl = 120L;
    /**
     * logo的资源位置
     */
    @Nullable
    private String logoPath;

    @PositiveOrZero
    private float logoRatio = 0.1F;
}
