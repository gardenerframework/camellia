package com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request;

import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/17 19:29
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BindAccountsToApplicationRolesParameter implements
        AccountTraits.AccountRelations<String>,
        SecurityTraits.ChallengeResponseTrait.ChallengeId,
        SecurityTraits.ChallengeResponseTrait.Response,
        SecurityTraits.TuringTraits.CaptchaToken {

    /**
     * 要绑定的账户id
     */
    @NotEmpty
    @NotNull
    @Valid
    private Collection<@NotBlank String> accountIds;
    /**
     * 要重新绑定的角色清单，为{@code null}标识解除所有角色
     */
    @Nullable
    @Valid
    private Collection<@Valid @NotNull ApplicationRoleProperty> roles;
    /**
     * 挑战token
     */
    private String challengeId;
    /**
     * 应答
     */
    private String response;
    /**
     * 人机检测token
     */
    private String captchaToken;
}
