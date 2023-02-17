package io.gardenerframework.camellia.authentication.server.main.schema.request;

import javax.servlet.http.HttpServletRequest;

/**
 * 从http参数中读取认证请求并转为目标类型的基类
 *
 * @author ZhangHan
 * @date 2022/4/26 16:58
 */
public abstract class AuthenticationRequestParameter {
    protected AuthenticationRequestParameter(HttpServletRequest request) {
    }
}
