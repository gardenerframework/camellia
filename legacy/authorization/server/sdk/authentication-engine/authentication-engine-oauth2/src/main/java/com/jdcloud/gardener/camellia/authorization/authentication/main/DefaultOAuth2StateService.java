package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.InvalidStateException;
import com.jdcloud.gardener.camellia.authorization.common.Version;
import com.jdcloud.gardener.fragrans.log.GenericBasicLogger;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Create;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericBasicLogContent;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/11/10 11:13
 */
@RequiredArgsConstructor
@Slf4j
public class DefaultOAuth2StateService implements OAuth2StateService {
    private final GenericBasicLogger basicLogger;

    @Override
    public void validate(
            @NonNull OAuth2AuthenticationServiceBase authenticationService,
            @NonNull HttpServletRequest httpRequest,
            @NonNull String state) throws InvalidStateException {
        HttpSession httpSession = Objects.requireNonNull(httpRequest.getSession());
        State stateInSession = (State) httpSession.getAttribute(getStateSessionKey(authenticationService));
        //检查state是否已经过期或者session中压根就没有
        if (stateInSession == null || stateInSession.getTimestamp().plus(stateInSession.getTtl()).isBefore(Instant.now())) {
            //移除state
            httpSession.removeAttribute(getStateSessionKey(authenticationService));
            throw new InvalidStateException(state);
        }
        //对比值
        if (!state.equals(stateInSession.getValue())) {
            throw new InvalidStateException(state);
        }
        //匹配正确，移除state
        httpSession.removeAttribute(getStateSessionKey(authenticationService));
    }

    @Override
    public String createState(OAuth2AuthenticationServiceBase authenticationService, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(true);
        State state = (State) session.getAttribute(getStateSessionKey(authenticationService));
        if (state == null || state.getTimestamp().plus(state.getTtl()).isBefore(Instant.now())) {
            //重新创建一个state
            state = new State(
                    Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)),
                    Instant.now(),
                    Duration.ofSeconds(300)
            );
            session.setAttribute(getStateSessionKey(authenticationService), state);
        }
        String stateValue = state.getValue();
        //记个日志
        basicLogger.debug(
                log,
                GenericBasicLogContent.builder()
                        .what(DefaultOAuth2StateService.State.class)
                        .how(new Create()).detail(
                                new Detail() {
                                    private final String sessionId = session.getId();
                                    private final String state = stateValue;
                                }
                        ).build(),
                null
        );
        return state.getValue();
    }

    private String getStateSessionKey(OAuth2AuthenticationServiceBase authenticationService) {
        return authenticationService.getClass().getCanonicalName();
    }

    /**
     * @author zhanghan30
     * @date 2022/11/9 16:04
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class State implements Serializable {
        private static final long serialVersionUID = Version.current;
        /**
         * 实际的state
         */
        private String value;
        /**
         * 创建时的时间戳
         */
        private Instant timestamp;
        /**
         * ttl
         */
        private Duration ttl;
    }
}
