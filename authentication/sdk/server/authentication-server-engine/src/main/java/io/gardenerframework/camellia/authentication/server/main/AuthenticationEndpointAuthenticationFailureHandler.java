package io.gardenerframework.camellia.authentication.server.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.exception.client.MfaAuthenticationRequiredException;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.common.configuration.AuthorizationServerPathOption;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import com.jdcloud.gardener.fragrans.log.schema.word.Word;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.exception.SpringHardCodedErrors;
import io.gardenerframework.camellia.authentication.server.main.schema.LoginAuthenticationRequestToken;
import io.gardenerframework.fragrans.api.standard.error.ApiErrorFactory;
import io.gardenerframework.fragrans.api.standard.error.DefaultApiErrorConstants;
import io.gardenerframework.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import io.gardenerframework.fragrans.api.standard.schema.ApiError;
import io.gardenerframework.fragrans.log.GenericLoggerStaticAccessor;
import io.gardenerframework.fragrans.log.common.schema.state.Failed;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.ApplicationListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhanghan30
 * @date 2021/12/27 9:44 下午
 */
@Data
@AllArgsConstructor
@Component
@Slf4j
public class AuthenticationEndpointAuthenticationFailureHandler implements AuthenticationFailureHandler, ApplicationListener<InitializingApiErrorPropertiesEvent> {
    private static final Map<String, HttpStatus> OAUTH2_ERROR_CODE_STATUS = new ConcurrentHashMap<>();
    private static final Pattern OAUTH2_PARAMETER_ERROR_PATTERN = Pattern.compile("OAuth 2.0 Parameter: (.+)$");
    private static final Pattern OAUTH2_CLIENT_AUTHENTICATION_ERROR_PATTERN = Pattern.compile("Client authentication failed: (.+)$");

    static {
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.INVALID_CLIENT, HttpStatus.UNAUTHORIZED);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT, HttpStatus.UNAUTHORIZED);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.ACCESS_DENIED, HttpStatus.UNAUTHORIZED);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.MFA_AUTHENTICATION_REQUIRED, HttpStatus.UNAUTHORIZED);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.INVALID_SCOPE, HttpStatus.BAD_REQUEST);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.INVALID_GRANT, HttpStatus.BAD_REQUEST);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.UNSUPPORTED_TOKEN_TYPE, HttpStatus.BAD_REQUEST);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.INSUFFICIENT_SCOPE, HttpStatus.BAD_REQUEST);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.UNSUPPORTED_RESPONSE_TYPE, HttpStatus.BAD_REQUEST);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE, HttpStatus.BAD_REQUEST);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        OAUTH2_ERROR_CODE_STATUS.put(OAuth2ErrorCodes.TEMPORARILY_UNAVAILABLE, HttpStatus.INTERNAL_SERVER_ERROR);

        //扫描类的静态变量
        Set<String> noStatusCodeFields = new HashSet<>();
        for (Field field : OAuth2ErrorCodes.class.getDeclaredFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers()) && String.class.equals(field.getType()) && OAUTH2_ERROR_CODE_STATUS.get(field.get(null)) == null) {
                    noStatusCodeFields.add(field.getName());
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        //要求所有报错编码必须明确指出http状态
        if (!CollectionUtils.isEmpty(noStatusCodeFields)) {
            throw new IllegalStateException(String.join(",", noStatusCodeFields) + " did not define a status code");
        }
    }

    private final AuthorizationServerPathOption authorizationServerPathOption;
    private final EnhancedMessageSource messageSource;
    private final ObjectMapper mapper;
    private final ApiErrorFactory apiErrorFactory;

    /**
     * 整体处理认证失败
     *
     * @param request   请求
     * @param response  响应
     * @param exception 失败错误
     * @throws IOException      io问题
     * @throws ServletException Servlet问题
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //最先打日志，看起来有时候会出现日志不打印的问题
        GenericLoggerStaticAccessor.operationLogger().debug(
                log,
                GenericOperationLogContent.builder()
                        .what(LoginAuthenticationRequestToken.class)
                        .operation(new Process())
                        .state(new Failed()).build(),
                exception
        );
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AuthenticationEndpointAuthenticationFailureHandler.class.getName(), true);
        //转为api error
        ApiError apiError = apiErrorFactory.createApiError(attributes, exception, LocaleContextHolder.getLocale());
        if (exception instanceof OAuth2AuthenticationException) {
            //处理OAth2认证问题
            handleTokenEndpointAuthenticationFailure(request, response, exception, apiError);
        } else {
            //处理web登录接口错误
            handleWebAuthenticationFailure(request, response, exception, apiError);
        }
    }

    /**
     * 处理oauth2的接口问题
     *
     * @param request   请求
     * @param response  响应
     * @param exception 失败错误
     * @throws IOException      io问题
     * @throws ServletException Servlet问题
     */
    private void handleTokenEndpointAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception, ApiError apiError) throws IOException, ServletException {
        OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        httpResponse.setStatusCode(Objects.requireNonNull(HttpStatus.resolve(apiError.getStatus())));
        error = new OAuth2Error(
                error.getErrorCode(),
                tryConvertSpringHardCodedDescription(error.getDescription(), apiError),
                null
        );
        //将OAuth2Error对象转为待输出的map
        //对于oauth2的异常，大部分都是AuthenticationEndpointExceptionAdapter转的，里面包着真正的认证异常
        //对于直接抛出的oauth2的异常，大部分也都是没有内部错误的
        mapper.writeValue(httpResponse.getBody(), convertOAuth2ExceptionToErrorAttributes(error, apiError));
    }

    /**
     * 处理spring 硬编码的oauth2错误描述
     *
     * @param description 描述
     * @param apiError    api error
     * @return 如果描述可转换，则转换描述，否则输出api error中的错误信息
     */
    private String tryConvertSpringHardCodedDescription(String description, ApiError apiError) {
        if (StringUtils.hasText(description)) {
            Matcher matcher = OAUTH2_PARAMETER_ERROR_PATTERN.matcher(description);
            if (matcher.find()) {
                String parameterName = matcher.group(1);
                return messageSource.getMessage(
                        new SpringHardCodedErrors.OAuth2ParameterError(parameterName),
                        messageSource.getMessage(DefaultApiErrorConstants.GENERIC_ERROR, LocaleContextHolder.getLocale()),
                        LocaleContextHolder.getLocale()
                );
            } else {
                matcher = OAUTH2_CLIENT_AUTHENTICATION_ERROR_PATTERN.matcher(description);
                if (matcher.find()) {
                    String parameterName = matcher.group(1);
                    return messageSource.getMessage(
                            new SpringHardCodedErrors.OAuth2ClientAuthenticationError(parameterName),
                            messageSource.getMessage(DefaultApiErrorConstants.GENERIC_ERROR, LocaleContextHolder.getLocale()),
                            LocaleContextHolder.getLocale()
                    );
                } else {
                    //add
                    GenericLoggerStaticAccessor.basicLogger().debug(
                            log,
                            GenericBasicLogContent.builder()
                                    .what(SpringHardCodedErrors.class)
                                    .how(new Unresolvable())
                                    .detail(new DescriptionDetail(description))
                                    .build(),
                            null
                    );
                    return messageSource.getMessage(DefaultApiErrorConstants.GENERIC_ERROR, LocaleContextHolder.getLocale());
                }
            }
        } else {
            return apiError.getMessage();
        }
    }

    /**
     * 完成{@link OAuth2Error}到{@link Map}，也就是错误属性的转换
     * <p>
     * 和{@link ErrorAttributes}做的差不多
     *
     * @param oauth2Error 错误
     * @return 错误属性
     */
    private Map<String, Object> convertOAuth2ExceptionToErrorAttributes(OAuth2Error oauth2Error, ApiError apiError) {
        Map<String, Object> errorAttributes = new HashMap<>(5);
        errorAttributes.put(OAuth2ParameterNames.ERROR, oauth2Error.getErrorCode());
        if (StringUtils.hasText(oauth2Error.getDescription())) {
            errorAttributes.put(OAuth2ParameterNames.ERROR_DESCRIPTION, oauth2Error.getDescription());
        }
        if (StringUtils.hasText(oauth2Error.getUri())) {
            errorAttributes.put(OAuth2ParameterNames.ERROR_URI, oauth2Error.getUri());
        }
        //added 增加输出错误的id
        errorAttributes.put("error_code", apiError.getError());
        errorAttributes.put("details", apiError.getDetails());
        return errorAttributes;
    }

    /**
     * 跳向mfa页面
     *
     * @param request                    请求
     * @param response                   响应
     * @param mfaAuthenticationChallenge mfa认证请求
     * @throws IOException      io问题
     * @throws ServletException Servlet问题
     */
    private void forwardToMfaWebPage(HttpServletRequest request, HttpServletResponse response, Challenge mfaAuthenticationChallenge) throws IOException, ServletException {
        if (!response.isCommitted()) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(authorizationServerPathOption.getWebMfaChallengePage());
            uriComponentsBuilder.queryParam("authenticator", UriUtils.encode(mfaAuthenticationChallenge.getAuthenticator(), "utf-8"));
            uriComponentsBuilder.queryParam("challengeId", UriUtils.encode(mfaAuthenticationChallenge.getId(), "utf-8"));
            uriComponentsBuilder.queryParam("expiresAt", UriUtils.encode(new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601).format(mfaAuthenticationChallenge.getExpiresAt()), "utf-8"));
            Map<String, ?> additionalPageParam = mfaAuthenticationChallenge.getParameters();
            if (additionalPageParam != null) {
                additionalPageParam.forEach(
                        (key, value) -> uriComponentsBuilder.queryParam(key, UriUtils.encode(String.valueOf(value), "utf-8"))
                );
            }
            response.sendRedirect(uriComponentsBuilder.build().toString());
        }
    }

    /**
     * 处理web登录接口的问题
     *
     * @param request   请求
     * @param response  响应
     * @param exception 失败错误
     * @throws IOException      io问题
     * @throws ServletException Servlet问题
     */
    private void handleWebAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception, ApiError apiError) throws IOException, ServletException {
        if (!response.isCommitted()) {
            if (exception instanceof MfaAuthenticationRequiredException) {
                //跳mfa认证页面
                forwardToMfaWebPage(request, response, ((MfaAuthenticationRequiredException) exception).getMfaAuthenticationChallenge());
            } else {
                UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(authorizationServerPathOption.getWebAuthenticationErrorPage());
                uriComponentsBuilder.queryParam("status", apiError.getStatus());
                uriComponentsBuilder.queryParam("phrase", apiError.getReason());
                uriComponentsBuilder.queryParam("message", UriUtils.encode(apiError.getMessage(), "utf-8"));
                uriComponentsBuilder.queryParam("code", UriUtils.encode(apiError.getError(), "utf-8"));
                if (apiError.getDetails() != null) {
                    String details = Base64.getUrlEncoder().encodeToString(mapper.writeValueAsString(apiError.getDetails()).getBytes(StandardCharsets.UTF_8));
                    uriComponentsBuilder.queryParam("details", details);
                }
                response.sendRedirect(uriComponentsBuilder.build().toString());
            }
        }
    }

    /**
     * 错错误异常获取状态码
     *
     * @param exception 错误异常
     * @return 状态码
     */
    private HttpStatus getStatus(Exception exception) {
        //查看是否有状态码注解
        ResponseStatus annotation = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            //没有注解，查看是否是包装类型
            if (exception instanceof ResponseStatusCodeAuthenticationException) {
                return ((ResponseStatusCodeAuthenticationException) exception).getStatus();
            }
            if (exception instanceof ResponseStatusCodeOAuth2AuthenticationException) {
                return ((ResponseStatusCodeOAuth2AuthenticationException) exception).getHttpStatus();
            }
        }
        //看看是否有内部原因
        Exception realCause = getRealCause(exception);
        //没有内部嵌套的人原因
        if (exception == realCause) {
            //处理一下OAuth2AuthenticationException
            if (realCause instanceof OAuth2AuthenticationException) {
                //拿着错误码查下映射表
                HttpStatus httpStatus = OAUTH2_ERROR_CODE_STATUS.get(((OAuth2AuthenticationException) realCause).getError().getErrorCode());
                if (httpStatus != null) {
                    //有就返回，没有就兜底
                    return httpStatus;
                } else {
                    //返回兜底错误
                    //兜底错误变更为http 400
                    return HttpStatus.BAD_REQUEST;
                }
            } else {
                //非OAuth2错误的兜底错误为401
                return realCause instanceof AuthenticationServiceException ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.UNAUTHORIZED;
            }
        } else {
            //查看内部的原因
            return realCause instanceof AuthenticationServiceException ? HttpStatus.INTERNAL_SERVER_ERROR : getStatus(realCause);
        }
    }

    /**
     * 获取异常的真正原因
     * <p>
     * 如果没有那原因就是exception自己了
     *
     * @param exception 异常
     * @return 原因
     */
    private Exception getRealCause(Exception exception) {
        Throwable cause = exception;
        while (isCauseContainer(cause)) {
            cause = cause.getCause();
        }
        //没有什么内涵的原因，因此原因就是给定的异常
        if (cause == null) {
            return exception;
        }
        return cause instanceof Exception ? (Exception) cause : exception;
    }

    /**
     * 判断是否是个错误的包装器
     *
     * @param throwable 错误
     * @return 是否是包装类
     */
    private boolean isCauseContainer(Throwable throwable) {
        return throwable instanceof ResponseStatusCodeAuthenticationException
                || throwable instanceof OAuth2AuthenticationException
                || throwable instanceof NestedAuthenticationException;
    }

    @Override
    public void onApplicationEvent(InitializingApiErrorPropertiesEvent event) {
        Object error = event.getError();
        Map<String, Object> errorAttributes = event.getErrorAttributes();
        if (errorAttributes == null || errorAttributes.get(AuthenticationEndpointAuthenticationFailureHandler.class.getName()) == null) {
            //不是由这个类要求factory输出的，不予处理
            return;
        }
        ApiError apiError = event.getApiError();
        //设置响应状态
        HttpStatus status = getStatus((Exception) error);
        apiError.setStatus(status.value());
        apiError.setReason(status.getReasonPhrase());
        Exception realCause = getRealCause((Exception) error);
        //将内部错误进行消息转换
        if (realCause != error) {
            Map<String, Object> realErrorAttributes = new HashMap<>();
            realErrorAttributes.put(AuthenticationEndpointAuthenticationFailureHandler.class.getName(), true);
            ApiError realError = apiErrorFactory.createApiError(realErrorAttributes, realCause, LocaleContextHolder.getLocale());
            apiError.setMessage(realError.getMessage());
            apiError.setDetails(realError.getDetails());
            apiError.setError(realError.getError());
            //状态码不覆盖
        } else {
            if (isCauseContainer((Exception) error)) {
                //如果没有什么内部原因，而且还是个包装类，那就没什么消息
                apiError.setMessage(null);
            }
        }
    }

    private static class Unresolvable implements Word {
        @Override
        public String toString() {
            return "无法解析";
        }
    }

    @AllArgsConstructor
    private static class DescriptionDetail implements Detail {
        /**
         * 描述
         */
        private String description;
    }
}
