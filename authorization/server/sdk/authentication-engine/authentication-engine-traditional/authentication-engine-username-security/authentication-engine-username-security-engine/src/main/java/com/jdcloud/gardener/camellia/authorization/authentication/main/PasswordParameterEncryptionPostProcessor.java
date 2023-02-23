package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.PasswordEncryptionKeyExpiredException;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.UsernamePasswordAuthenticationParameter;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author zhanghan30
 * @date 2022/12/26 17:35
 */
public class PasswordParameterEncryptionPostProcessor implements UsernamePasswordAuthenticationParameterPostProcessor {
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private PasswordEncryptionService encryptionService;

    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private PasswordEncryptionKeyStore keyStore;

    /**
     * 当参数被{@link javax.xml.validation.Validator}验证通过后进行后置处理
     *
     * @param parameter 认证参数
     * @throws AuthenticationException 如果在过程中认为认证应该失败，则抛出认证异常
     */
    @Override
    public void afterParameterValidated(UsernamePasswordAuthenticationParameter parameter) throws AuthenticationException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            HttpSession session = request.getSession(true);
            String sessionId = session.getId();
            String key = keyStore.load(sessionId);
            if (key == null) {
                //加密秘钥已经过期
                throw new PasswordEncryptionKeyExpiredException(sessionId);
            }
            //执行解密
            try {
                parameter.setPassword(encryptionService.decrypt(key, parameter.getPassword()));
            } catch (Exception e) {
                throw new AuthenticationServiceException(e.getMessage(), e);
            }
        }
    }
}
