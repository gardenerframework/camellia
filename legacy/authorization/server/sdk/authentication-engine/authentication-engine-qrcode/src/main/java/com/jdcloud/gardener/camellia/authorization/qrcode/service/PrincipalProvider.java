package com.jdcloud.gardener.camellia.authorization.qrcode.service;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

/**
 * 从http请求中给出具体的登录名
 *
 * @author ZhangHan
 * @date 2021/12/23 3:09
 */
@FunctionalInterface
public interface PrincipalProvider<P extends BasicPrincipal> extends Function<HttpServletRequest, P> {
}
