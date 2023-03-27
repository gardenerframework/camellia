package io.gardenerframework.camellia.authentication.server.common.configuration;

import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 认证服务器路径选项
 *
 * @author ZhangHan
 * @date 2022/5/11 12:45
 */
@ApiOption(readonly = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class AuthenticationServerPathOption {
    /**
     * 管理接口地址
     */
    private String administrationRestApiContextPath = "/administration";
    /**
     * rest api的上下文路径
     */
    private String authenticationRestApiContextPath = "/api";
    /**
     * 网页端登录入口的地址
     * <p>
     * 只有POST会处理
     */
    private String webAuthenticationEndpoint = "/login";
    /**
     * 登录网页的地址
     */
    private String webLoginPage = "/";
    /**
     * 登录网页的地址
     */
    private String webLoginSuccessPage = "/welcome";
    /**
     * web应用类型的认证错误跳向的错误页面
     */
    private String webAuthenticationErrorPage = "/error";
    /**
     * 需要mfa多因子验证时转向的页面
     */
    private String webMfaChallengePage = "/mfa";
    /**
     * 成功登出后的跳转地址
     */
    private String webLogoutPage = "/goodbye";
    /**
     * 网页登出地址
     * <p>
     * 什么请求类型可以
     */
    private String webLogoutEndpoint = "/logout";

    /**
     * oauth2的授权申请接口
     */
    private String oAuth2AuthorizationEndpoint = "/oauth2/authorize";
    /**
     * oauth2的授权批准网页地址
     */
    private String oAuth2AuthorizationConsentPage = "/consent";
    /**
     * oauth2的令牌接口
     */
    private String oAuth2TokenEndpoint = "/oauth2/token";
    /**
     * oidc的用户信息接口
     */
    private String oidcUserInfoEndpoint = "/userinfo";
}
