package io.gardenerframework.camellia.authentication.server.main.endpoint;

import io.gardenerframework.camellia.authentication.server.common.api.group.AuthenticationServerRestController;
import io.gardenerframework.camellia.authentication.server.main.PasswordEncryptionService;
import io.gardenerframework.camellia.authentication.server.main.configuration.PasswordEncryptionServiceComponent;
import io.gardenerframework.camellia.authentication.server.main.exception.NestedAuthenticationException;
import io.gardenerframework.camellia.authentication.server.main.schema.request.AuthenticationRequestParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.request.UsernamePasswordAuthenticationParameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.function.BiConsumer;

@AuthenticationServerRestController
@PasswordEncryptionServiceComponent
@AllArgsConstructor
@RequestMapping("/authentication/username-password/key")
public class PasswordEncryptionKeyEndpoint implements BiConsumer<HttpServletRequest, UsernamePasswordAuthenticationParameter> {
    private final static String COOKIE_NAME = "password-encryption-key-id";
    private final Validator validator;
    private final PasswordEncryptionService passwordEncryptionService;

    /**
     * 返回加密秘钥
     *
     * @return 秘钥
     * @throws Exception 遇到问题
     */
    @PostMapping
    public PasswordEncryptionService.Key createKey(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PasswordEncryptionService.Key key = passwordEncryptionService.createKey();
        Cookie cookie = new Cookie(COOKIE_NAME, key.getId());
        //js脚本不可见
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) ((key.getExpiryTime().getTime() - new Date().getTime()) / 1000));
        cookie.setDomain(request.getServerName());
        cookie.setPath("/");
        response.addCookie(cookie);
        return passwordEncryptionService.createKey();
    }

    /**
     * 处理密码解密
     *
     * @param request                                 the first input argument
     * @param usernamePasswordAuthenticationParameter the second input argument
     */
    @Override
    public void accept(HttpServletRequest request, UsernamePasswordAuthenticationParameter usernamePasswordAuthenticationParameter) {
        //取id
        PasswordEncryptionKeyIdParameter passwordEncryptionKeyIdParameter = new PasswordEncryptionKeyIdParameter(request);
        //执行验证
        passwordEncryptionKeyIdParameter.validate(validator);
        try {
            //把解密后的密码放进去
            usernamePasswordAuthenticationParameter.setPassword(
                    passwordEncryptionService.decrypt(
                            passwordEncryptionKeyIdParameter.getPasswordEncryptionKeyId(),
                            usernamePasswordAuthenticationParameter.getPassword())
            );
        } catch (Exception e) {
            throw new NestedAuthenticationException(e);
        }
    }

    private static class PasswordEncryptionKeyIdParameter extends AuthenticationRequestParameter {
        @NotBlank
        @Getter
        private String passwordEncryptionKeyId;

        protected PasswordEncryptionKeyIdParameter(HttpServletRequest request) {
            super(request);
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    //提交上来的cookie没有什么http only
                    if (COOKIE_NAME.equals(cookie.getName())) {
                        passwordEncryptionKeyId = cookie.getValue();
                    }
                }
            }
            //cookie没有读参数
            if (passwordEncryptionKeyId == null) {
                passwordEncryptionKeyId = request.getParameter("passwordEncryptionKeyId");
            }
        }
    }
}
