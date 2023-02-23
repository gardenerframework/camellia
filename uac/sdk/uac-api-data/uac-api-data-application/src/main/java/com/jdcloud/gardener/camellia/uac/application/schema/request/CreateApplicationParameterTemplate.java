package com.jdcloud.gardener.camellia.uac.application.schema.request;

import com.jdcloud.gardener.camellia.uac.application.schema.trait.BasicApplicationInformation;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * @author ZhangHan
 * @date 2022/11/8 13:48
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CreateApplicationParameterTemplate implements
        BasicApplicationInformation,
        SecurityTraits.TuringTraits.CaptchaToken,
        SecurityTraits.ChallengeResponseTrait.ChallengeId,
        SecurityTraits.ChallengeResponseTrait.Response {
    /**
     * 应用的名称
     */
    @NotBlank
    private String name;
    /**
     * 剪短的一些描述
     */
    private String description;
    /**
     * 主页地址
     */
    private String homepageUrl;
    /**
     * 图标
     */
    private String logo;
    /**
     * 图灵测试token
     */
    private String captchaToken;
    /**
     *
     */
    private String challengeId;
    /**
     * 应答
     */
    private String response;
}
