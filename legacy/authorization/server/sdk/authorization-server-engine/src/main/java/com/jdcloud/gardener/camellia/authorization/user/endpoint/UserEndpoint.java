package com.jdcloud.gardener.camellia.authorization.user.endpoint;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticatedAuthentication;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.common.api.group.AuthorizationServerRestController;
import com.jdcloud.gardener.camellia.authorization.common.configuration.AuthorizationServerPathOption;
import com.jdcloud.gardener.camellia.authorization.user.schema.response.UserAppearance;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.client.UnauthorizedException;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericBasicLogContent;
import com.jdcloud.gardener.fragrans.log.schema.word.Word;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhanghan30
 * @date 2021/12/24 9:21 下午
 */
@Component
@AuthorizationServerRestController
@RequestMapping("/me")
@Slf4j
@AllArgsConstructor
public class UserEndpoint {
    private final AuthorizationServerPathOption authorizationServerPathOption;
    private final Converter<? extends User, ? extends UserAppearance> userToVoConverter;

    @GetMapping
    public UserAppearance readUserInfo(HttpServletResponse response) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException();
        }
        if (authentication instanceof UserAuthenticatedAuthentication) {
            User user = ((UserAuthenticatedAuthentication) authentication).getUser();
            if (user == null) {
                throw new UnauthorizedException();
            }
            return castConverter().convert(user);
        } else if (authentication instanceof AbstractOAuth2TokenAuthenticationToken) {
            //带着token过来的重定向userinfo接口
            response.sendRedirect(authorizationServerPathOption.getOidcUserInfoEndpoint());
            return null;
        } else {
            GenericLoggerStaticAccessor.basicLogger().debug(
                    log,
                    GenericBasicLogContent.builder()
                            .what(authentication.getClass())
                            .how(new Word() {
                                @Override
                                public String toString() {
                                    return "不支持";
                                }
                            }).build(),
                    null
            );
            throw new UnauthorizedException();
        }
    }

    @SuppressWarnings("unchecked")
    private Converter<User, UserAppearance> castConverter() {
        return (Converter<User, UserAppearance>) userToVoConverter;
    }
}
