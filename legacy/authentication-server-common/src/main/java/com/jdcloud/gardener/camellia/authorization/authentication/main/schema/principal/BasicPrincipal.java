package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal;

import com.jdcloud.gardener.camellia.authorization.common.Version;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.security.Principal;

/**
 * 基础登录名
 * <p>
 * 其它的子类分别代表一种登录形式
 * <p>
 * 比如用户名
 * <p>
 * 比如手机号
 * <p>
 * 比如邮箱
 * <p>
 * 一个用户(也称主体)，允许有多种不同的登录名
 *
 * @author ZhangHan
 * @date 2022/1/1 0:43
 */
@Data
@AllArgsConstructor
@SuperBuilder
@LogTarget("登录名")
@EqualsAndHashCode
public abstract class BasicPrincipal implements Principal, Serializable {
    private static final long serialVersionUID = Version.current;
    private final String name;
}
