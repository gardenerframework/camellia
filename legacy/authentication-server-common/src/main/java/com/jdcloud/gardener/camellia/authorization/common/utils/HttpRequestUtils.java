package com.jdcloud.gardener.camellia.authorization.common.utils;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author ZhangHan
 * @date 2022/5/15 21:46
 */
public abstract class HttpRequestUtils {
    /**
     * 敏感的http头
     */
    private static final Collection<String> SENSITIVE_HEADERS = new HashSet<>();

    static {
        SENSITIVE_HEADERS.addAll(Arrays.asList(
                //认证头是任何插件和组件都不需要的，没有任何代码有理由要求认证头
                "Authorization"
        ));
    }

    private HttpRequestUtils() {

    }

    /**
     * 获取安全的 http 头
     * <p>
     * 修改这个头的信息不会影响外面的http请求的任何东西
     *
     * @param request http 请求
     * @return http 头清单
     */
    public static MultiValueMap<String, String> getSafeHttpHeaders(HttpServletRequest request) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Collection<String> headerNames = Collections.list(request.getHeaderNames());
        headerNames.forEach(
                headerName -> SENSITIVE_HEADERS.forEach(
                        sensitiveHeaderName -> {
                            if (sensitiveHeaderName.compareToIgnoreCase(headerName) != 0) {
                                headers.put(headerName, Collections.list(request.getHeaders(headerName)));
                            }
                        }
                )
        );
        return headers;
    }
}
