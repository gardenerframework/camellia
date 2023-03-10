package io.gardenerframework.camellia.authentication.server.main.event.schema;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * 客户端已经通过Spring Security的认证
 * <p>
 * 开发人员自行完成认证逻辑
 * <p>
 * 比如当前客户端是否有问题，需要停止一段时间
 * <p>
 * 客户端是否已经封了，不让用了
 *
 * @author zhanghan30
 * @date 2022/5/12 7:24 下午
 */
@Getter
@SuperBuilder
public class ClientAuthenticatedEvent extends AuthenticationEvent {
}
