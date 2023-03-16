package io.gardenerframework.camellia.authentication.server.main.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * @author zhanghan30
 * @date 2023/3/16 14:18
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class QrCodeBasedAuthenticationServiceOption {
    /**
     * 是否去创建图片
     */
    private boolean createImage = true;
    /**
     * 落地页地址
     */
    @NotBlank
    private String landingPageUrl;
    /**
     * 中间小图标的地址
     */
    @Nullable
    private String logoPath;
    /**
     * 二维码的有效期
     * <p>
     * 默认2分钟
     */
    @Positive
    private long ttl = 120L;
}
