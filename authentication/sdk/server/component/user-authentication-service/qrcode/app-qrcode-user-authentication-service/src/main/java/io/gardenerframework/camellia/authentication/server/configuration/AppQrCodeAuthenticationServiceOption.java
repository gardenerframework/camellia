package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
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
     * 二维码的边距
     */
    @Positive
    private int margin = 3;
    /**
     * 落地页地址
     * <p>
     * 作为app扫码的落地页不允许为空地址
     */
    @NotBlank
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
    private float logoRatio = 0.15F;
}
