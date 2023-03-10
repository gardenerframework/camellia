package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.*;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.LoginAuthenticationRequestToken;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.exception.client.MfaAuthenticationRequiredException;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.common.configuration.AuthorizationServerPathOption;
import com.jdcloud.gardener.fragrans.api.standard.error.ApiErrorFactory;
import com.jdcloud.gardener.fragrans.api.standard.error.DefaultApiErrorConstants;
import com.jdcloud.gardener.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import com.jdcloud.gardener.fragrans.api.standard.schema.ApiError;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Failed;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Process;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericBasicLogContent;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import com.jdcloud.gardener.fragrans.log.schema.word.Word;
import com.jdcloud.gardener.fragrans.messages.EnhancedMessageSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
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
 * @date 2021/12/27 9:44 ??????
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

        //????????????????????????
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
        //??????????????????????????????????????????http??????
        if (!CollectionUtils.isEmpty(noStatusCodeFields)) {
            throw new IllegalStateException(String.join(",", noStatusCodeFields) + " did not define a status code");
        }
    }

    private final AuthorizationServerPathOption authorizationServerPathOption;
    private final EnhancedMessageSource messageSource;
    private final ObjectMapper mapper;
    private final ApiErrorFactory apiErrorFactory;

    /**
     * ????????????????????????
     *
     * @param request   ??????
     * @param response  ??????
     * @param exception ????????????
     * @throws IOException      io??????
     * @throws ServletException Servlet??????
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //?????????????????????????????????????????????????????????????????????
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
        //??????api error
        ApiError apiError = apiErrorFactory.createApiError(attributes, exception, LocaleContextHolder.getLocale());
        if (exception instanceof OAuth2AuthenticationException) {
            //??????OAth2????????????
            handleTokenEndpointAuthenticationFailure(request, response, exception, apiError);
        } else {
            //??????web??????????????????
            handleWebAuthenticationFailure(request, response, exception, apiError);
        }
    }

    /**
     * ??????oauth2???????????????
     *
     * @param request   ??????
     * @param response  ??????
     * @param exception ????????????
     * @throws IOException      io??????
     * @throws ServletException Servlet??????
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
        //???OAuth2Error????????????????????????map
        //??????oauth2???????????????????????????AuthenticationEndpointExceptionAdapter??????????????????????????????????????????
        //?????????????????????oauth2???????????????????????????????????????????????????
        mapper.writeValue(httpResponse.getBody(), convertOAuth2ExceptionToErrorAttributes(error, apiError));
    }

    /**
     * ??????spring ????????????oauth2????????????
     *
     * @param description ??????
     * @param apiError    api error
     * @return ??????????????????????????????????????????????????????api error??????????????????
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
     * ??????{@link OAuth2Error}???{@link Map}?????????????????????????????????
     * <p>
     * ???{@link ErrorAttributes}???????????????
     *
     * @param oauth2Error ??????
     * @return ????????????
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
        //added ?????????????????????id
        errorAttributes.put("error_code", apiError.getError());
        errorAttributes.put("details", apiError.getDetails());
        return errorAttributes;
    }

    /**
     * ??????mfa??????
     *
     * @param request                    ??????
     * @param response                   ??????
     * @param mfaAuthenticationChallenge mfa????????????
     * @throws IOException      io??????
     * @throws ServletException Servlet??????
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
     * ??????web?????????????????????
     *
     * @param request   ??????
     * @param response  ??????
     * @param exception ????????????
     * @throws IOException      io??????
     * @throws ServletException Servlet??????
     */
    private void handleWebAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception, ApiError apiError) throws IOException, ServletException {
        if (!response.isCommitted()) {
            if (exception instanceof MfaAuthenticationRequiredException) {
                //???mfa????????????
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
     * ??????????????????????????????
     *
     * @param exception ????????????
     * @return ?????????
     */
    private HttpStatus getStatus(Exception exception) {
        //??????????????????????????????
        ResponseStatus annotation = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            //??????????????????????????????????????????
            if (exception instanceof ResponseStatusCodeAuthenticationException) {
                return ((ResponseStatusCodeAuthenticationException) exception).getStatus();
            }
            if (exception instanceof ResponseStatusCodeOAuth2AuthenticationException) {
                return ((ResponseStatusCodeOAuth2AuthenticationException) exception).getHttpStatus();
            }
        }
        //???????????????????????????
        Exception realCause = getRealCause(exception);
        //??????????????????????????????
        if (exception == realCause) {
            //????????????OAuth2AuthenticationException
            if (realCause instanceof OAuth2AuthenticationException) {
                //??????????????????????????????
                HttpStatus httpStatus = OAUTH2_ERROR_CODE_STATUS.get(((OAuth2AuthenticationException) realCause).getError().getErrorCode());
                if (httpStatus != null) {
                    //??????????????????????????????
                    return httpStatus;
                } else {
                    //??????????????????
                    //?????????????????????http 400
                    return HttpStatus.BAD_REQUEST;
                }
            } else {
                //???OAuth2????????????????????????401
                return realCause instanceof AuthenticationServiceException ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.UNAUTHORIZED;
            }
        } else {
            //?????????????????????
            return realCause instanceof AuthenticationServiceException ? HttpStatus.INTERNAL_SERVER_ERROR : getStatus(realCause);
        }
    }

    /**
     * ???????????????????????????
     * <p>
     * ???????????????????????????exception?????????
     *
     * @param exception ??????
     * @return ??????
     */
    private Exception getRealCause(Exception exception) {
        Throwable cause = exception;
        while (isCauseContainer(cause)) {
            cause = cause.getCause();
        }
        //???????????????????????????????????????????????????????????????
        if (cause == null) {
            return exception;
        }
        return cause instanceof Exception ? (Exception) cause : exception;
    }

    /**
     * ????????????????????????????????????
     *
     * @param throwable ??????
     * @return ??????????????????
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
            //????????????????????????factory????????????????????????
            return;
        }
        ApiError apiError = event.getApiError();
        //??????????????????
        HttpStatus status = getStatus((Exception) error);
        apiError.setStatus(status.value());
        apiError.setReason(status.getReasonPhrase());
        Exception realCause = getRealCause((Exception) error);
        //?????????????????????????????????
        if (realCause != error) {
            Map<String, Object> realErrorAttributes = new HashMap<>();
            realErrorAttributes.put(AuthenticationEndpointAuthenticationFailureHandler.class.getName(), true);
            ApiError realError = apiErrorFactory.createApiError(realErrorAttributes, realCause, LocaleContextHolder.getLocale());
            apiError.setMessage(realError.getMessage());
            apiError.setDetails(realError.getDetails());
            apiError.setError(realError.getError());
            //??????????????????
        } else {
            if (isCauseContainer((Exception) error)) {
                //?????????????????????????????????????????????????????????????????????????????????
                apiError.setMessage(null);
            }
        }
    }

    private static class Unresolvable implements Word {
        @Override
        public String toString() {
            return "????????????";
        }
    }

    @AllArgsConstructor
    private static class DescriptionDetail implements Detail {
        /**
         * ??????
         */
        private String description;
    }
}
