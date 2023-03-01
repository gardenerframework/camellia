package com.jdcloud.gardener.camellia.authorization.qrcode.configuration;

import com.jdcloud.gardener.fragrans.api.options.schema.ApiOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * @author zhanghan30
 * @date 2021/12/22 11:17 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiOption(readonly = false)
@Component
public class QrCodeOption {
    /**
     * 落地页url
     */
    @NotBlank
    private String url;
    /**
     * token有效期
     */
    @Positive
    private int tokenTtl = 120;
    /**
     * 默认的二维码大小
     */
    @Positive
    private int size = 300;
    /**
     * 默认白色北京
     */
    private int backgroundColor = -1;
    /**
     * 编码颜色
     */
    private int codeColor = 0;
    /**
     * 默认的二维码logo
     */
    @NotBlank
    private String logoPath = "authorization-server-authentication-engine/qrcode/logo.png";
    /**
     * 默认的二维码logo比例
     */
    @Max(value = 1L)
    @Positive
    private float logoSizePercentage = 0.2F;
    /**
     * 等待登录确认的最大秒数
     */
    @Positive
    private int maxSecondsWaitForConfirming = 20;

    /**
     * 扫描确认后留给登录页跳转页面进行检查的最大秒数
     */
    @Positive
    private int maxSecondsWaitForAuthentication = 10;
}

