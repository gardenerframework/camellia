package io.gardenerframework.camellia.authentication.server.main.spring.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.configuration.OAuth2AuthorizationConsentOption;
import io.gardenerframework.fragrans.data.cache.client.RedisCacheClient;
import io.gardenerframework.fragrans.data.cache.lock.CacheLock;
import io.gardenerframework.fragrans.data.cache.lock.context.LockContext;
import io.gardenerframework.fragrans.data.cache.lock.context.ServletRequestLockContextHolder;
import io.gardenerframework.fragrans.data.cache.serialize.JdkSerializer;
import io.gardenerframework.fragrans.data.cache.serialize.LongSerializer;
import io.gardenerframework.fragrans.data.cache.serialize.StringSerializer;
import io.gardenerframework.fragrans.log.GenericLoggerStaticAccessor;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Delete;
import io.gardenerframework.fragrans.log.common.schema.verb.Read;
import io.gardenerframework.fragrans.log.common.schema.verb.Start;
import io.gardenerframework.fragrans.log.common.schema.verb.Update;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author ZhangHan
 * @date 2022/1/8 3:48
 */
@Slf4j
@AuthenticationServerEngineComponent
public class CachedOAuth2AuthorizationService implements OAuth2AuthorizationService {
    private static final String NAMESPACE_HEADER = "camellia:authentication:server:engine:token:";
    private final RedisCacheClient cacheClient;
    private final OAuth2AuthorizationConsentOption options;
    private final StringSerializer scriptKeySerializer = new StringSerializer();
    private final JdkSerializer<OAuth2Authorization> valueSerializer = new JdkSerializer<>();
    private final CacheLock cacheLock;
    private String queryScript;
    private String updateScript;
    private String deleteScript;

    public CachedOAuth2AuthorizationService(RedisCacheClient client, OAuth2AuthorizationConsentOption options) throws IOException {
        Assert.isTrue(
                client.supportLuaScript(),
                "client must be RedisCacheClient and support lua script"
        );
        this.cacheClient = client;
        //锁的上下文在一次请求内有效
        this.cacheLock = new CacheLock(cacheClient, new ServletRequestLockContextHolder());
        loadQueryScript();
        loadUpdateScript();
        loadDeleteScript();
        this.options = options;
    }

    /**
     * 加载lua脚本
     *
     * @param path 路径
     * @return 脚本内容
     */
    private String loadScriptFile(String path) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(path).getInputStream()));
            StringBuilder stringBuffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void loadQueryScript() throws IOException {
        if (this.queryScript == null || !cacheClient.scriptExists(this.queryScript)) {
            this.queryScript = this.cacheClient.loadLuaScriptFile("authentication-server-engine/scripts/oauth2-authorization-service/read-by-index.lua");
        }
    }

    /**
     * 加载覆盖脚本
     */
    private void loadUpdateScript() throws IOException {
        if (this.updateScript == null || !this.cacheClient.scriptExists(updateScript)) {
            this.updateScript = this.cacheClient.loadLuaScriptFile("authentication-server-engine/scripts/oauth2-authorization-service/update.lua");
        }
    }

    private void loadDeleteScript() throws IOException {
        if (this.deleteScript == null || !this.cacheClient.scriptExists(deleteScript)) {
            this.deleteScript = this.cacheClient.loadLuaScriptFile("authentication-server-engine/scripts/oauth2-authorization-service/remove.lua");
        }
    }

    private String composeTokenKey(String token, String type) {
        if (!StringUtils.hasText(token)) {
            return "";
        }
        return String.format(NAMESPACE_HEADER + "{%s}.%s", token, type);
    }

    private String composeIdKey(String id) {
        return NAMESPACE_HEADER + String.format("{%s}.object", id);
    }

    private String composeLockKey(String id) {
        return NAMESPACE_HEADER + String.format("{%s}.lock", id);
    }

    private long calculateTtl(Instant issueAt, Instant expiresAt) {
        if (expiresAt == null) {
            return 0;
        } else {
            return Duration.between(issueAt, expiresAt).getSeconds();
        }
    }

    private String extractTokenValue(OAuth2Authorization.Token<?> token) {
        if (token == null) {
            return "";
        } else {
            return token.getToken().getTokenValue();
        }
    }

    private long extractTokenTtl(OAuth2Authorization.Token<?> token) {
        if (token == null) {
            return 0;
        } else {
            return calculateTtl(token.getToken().getIssuedAt(), token.getToken().getExpiresAt());
        }
    }

    /**
     * 读取出已经保存的key
     * <p>
     * 即使不删除的key也要占位
     *
     * @param authorization 认证
     * @return 要删除的key
     */
    private Map<String, TokenEssentials> buildIndex(@Nullable OAuth2Authorization authorization) {
        Map<String, TokenEssentials> index = new HashMap<>(5);
        if (authorization == null) {
            //为空全部返回默认值
            index.put(OAuth2ParameterNames.STATE, new TokenEssentials());
            index.put(OAuth2ParameterNames.CODE, new TokenEssentials());
            index.put(OAuth2TokenType.ACCESS_TOKEN.getValue(), new TokenEssentials());
            index.put(OidcParameterNames.ID_TOKEN, new TokenEssentials());
            index.put(OAuth2TokenType.REFRESH_TOKEN.getValue(), new TokenEssentials());
        } else {
            //否则返回新的值
            index.put(OAuth2ParameterNames.STATE, new TokenEssentials(authorization.getAttribute(OAuth2ParameterNames.STATE) == null ? "" : authorization.getAttribute(OAuth2ParameterNames.STATE), authorization.getAttribute(OAuth2ParameterNames.STATE) == null ? 0 : options.getConsentStateTtl()));
            index.put(OAuth2ParameterNames.CODE, new TokenEssentials(extractTokenValue(authorization.getToken(OAuth2AuthorizationCode.class)), extractTokenTtl(authorization.getToken(OAuth2AuthorizationCode.class))));
            index.put(OAuth2TokenType.ACCESS_TOKEN.getValue(), new TokenEssentials(extractTokenValue(authorization.getAccessToken()), extractTokenTtl(authorization.getAccessToken())));
            index.put(OidcParameterNames.ID_TOKEN, new TokenEssentials(extractTokenValue(authorization.getToken(OidcIdToken.class)), extractTokenTtl(authorization.getToken(OidcIdToken.class))));
            index.put(OAuth2TokenType.REFRESH_TOKEN.getValue(), new TokenEssentials(extractTokenValue(authorization.getRefreshToken()), extractTokenTtl(authorization.getRefreshToken())));
        }
        return index;
    }

    private long calculateMaxTtl(Map<String, TokenEssentials> index) {
        long ttl = 0;
        for (TokenEssentials tokenEssentials : index.values()) {
            if (tokenEssentials.getTtl() > ttl) {
                ttl = tokenEssentials.getTtl();
            }
        }
        return ttl;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        GenericLoggerStaticAccessor.operationLogger().debug(
                log,
                GenericOperationLogContent.builder()
                        .what(OAuth2Authorization.class)
                        .operation(new Update())
                        .state(new Start())
                        .detail(new OAuth2AuthorizationDetail(authorization))
                        .build(),
                null
        );
        Duration lockTtl = Duration.ofSeconds(20);
        //对待更新的授权id上锁，因为要先读取后更新
        LockContext context = cacheLock.lock(composeLockKey(authorization.getId()), lockTtl);
        Instant start = Instant.now();
        try {
            //读取出之前保存的授权
            Map<String, TokenEssentials> needToDelete = buildIndex(findById(authorization.getId()));
            Map<String, TokenEssentials> needToCreate = buildIndex(authorization);
            cacheClient.executeScript(
                    //更新脚本hash
                    updateScript,
                    //一共要处理11个key
                    11,
                    //id key - 也就是授权实际保存的位置
                    scriptKeySerializer.serialize(composeIdKey(authorization.getId())),
                    //删除的旧索引key
                    //state索引
                    scriptKeySerializer.serialize(composeTokenKey(needToDelete.get(OAuth2ParameterNames.STATE).getValue(), OAuth2ParameterNames.STATE)),
                    //授权码索引
                    scriptKeySerializer.serialize(composeTokenKey(needToDelete.get(OAuth2ParameterNames.CODE).getValue(), OAuth2ParameterNames.CODE)),
                    //授权令牌索引
                    scriptKeySerializer.serialize(composeTokenKey(needToDelete.get(OAuth2TokenType.ACCESS_TOKEN.getValue()).getValue(), OAuth2TokenType.ACCESS_TOKEN.getValue())),
                    //id令牌索引
                    scriptKeySerializer.serialize(composeTokenKey(needToDelete.get(OidcParameterNames.ID_TOKEN).getValue(), OidcParameterNames.ID_TOKEN)),
                    //刷新令牌索引
                    scriptKeySerializer.serialize(composeTokenKey(needToDelete.get(OAuth2TokenType.REFRESH_TOKEN.getValue()).getValue(), OAuth2TokenType.REFRESH_TOKEN.getValue())),
                    //添加的key
                    //state索引
                    scriptKeySerializer.serialize(composeTokenKey(needToCreate.get(OAuth2ParameterNames.STATE).getValue(), OAuth2ParameterNames.STATE)),
                    //授权码索引
                    scriptKeySerializer.serialize(composeTokenKey(needToCreate.get(OAuth2ParameterNames.CODE).getValue(), OAuth2ParameterNames.CODE)),
                    //授权令牌索引
                    scriptKeySerializer.serialize(composeTokenKey(needToCreate.get(OAuth2TokenType.ACCESS_TOKEN.getValue()).getValue(), OAuth2TokenType.ACCESS_TOKEN.getValue())),
                    //id令牌索引
                    scriptKeySerializer.serialize(composeTokenKey(needToCreate.get(OidcParameterNames.ID_TOKEN).getValue(), OidcParameterNames.ID_TOKEN)),
                    //刷新令牌索引
                    scriptKeySerializer.serialize(composeTokenKey(needToCreate.get(OAuth2TokenType.REFRESH_TOKEN.getValue()).getValue(), OAuth2TokenType.REFRESH_TOKEN.getValue())),
                    //授权的值
                    valueSerializer.serialize(authorization),
                    //批准状态的ttl
                    new LongSerializer().serialize(options.getConsentStateTtl()),
                    //新授权的ttl(应当是以上索引的要求的ttl中的最长的值)
                    new LongSerializer().serialize(calculateMaxTtl(needToCreate))
            );
            GenericLoggerStaticAccessor.operationLogger().debug(
                    log,
                    GenericOperationLogContent.builder()
                            .what(OAuth2Authorization.class)
                            .operation(new Update())
                            .state(new Done())
                            .detail(new OAuth2AuthorizationDetail(authorization))
                            .build(),
                    null
            );
        } finally {
            Instant completed = Instant.now();
            long timing = Duration.between(start, completed).getSeconds();
            if (timing <= lockTtl.getSeconds()) {
                cacheLock.releaseLock(composeLockKey(authorization.getId()), context);
            } else {
                //什么也不做
            }
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        GenericLoggerStaticAccessor.operationLogger().debug(
                log,
                GenericOperationLogContent.builder()
                        .what(OAuth2Authorization.class)
                        .operation(new Delete())
                        .state(new Start())
                        .detail(new OAuth2AuthorizationDetail(authorization))
                        .build(),
                null
        );
        Map<String, TokenEssentials> needToDelete = buildIndex(findById(authorization.getId()));
        cacheClient.executeScript(
                deleteScript,
                6,
                //id key
                scriptKeySerializer.serialize(composeIdKey(authorization.getId())),
                //删除的旧key
                scriptKeySerializer.serialize(composeTokenKey(needToDelete.get(OAuth2ParameterNames.STATE).getValue(), OAuth2ParameterNames.STATE)),
                scriptKeySerializer.serialize(composeTokenKey(needToDelete.get(OAuth2ParameterNames.CODE).getValue(), OAuth2ParameterNames.CODE)),
                scriptKeySerializer.serialize(composeTokenKey(needToDelete.get(OAuth2TokenType.ACCESS_TOKEN.getValue()).getValue(), OAuth2TokenType.ACCESS_TOKEN.getValue())),
                scriptKeySerializer.serialize(composeTokenKey(needToDelete.get(OidcParameterNames.ID_TOKEN).getValue(), OidcParameterNames.ID_TOKEN)),
                scriptKeySerializer.serialize(composeTokenKey(needToDelete.get(OAuth2TokenType.REFRESH_TOKEN.getValue()).getValue(), OAuth2TokenType.REFRESH_TOKEN.getValue()))
        );
        GenericLoggerStaticAccessor.operationLogger().debug(
                log,
                GenericOperationLogContent.builder()
                        .what(OAuth2Authorization.class)
                        .operation(new Delete())
                        .state(new Done())
                        .detail(new OAuth2AuthorizationDetail(authorization))
                        .build(),
                null
        );
    }

    @Override
    public OAuth2Authorization findById(String id) {
        return valueSerializer.deserialize(cacheClient.get(composeIdKey(id)));
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Set<String> lookupTokenTypes = new HashSet<>(4);
        GenericLoggerStaticAccessor.operationLogger().debug(
                log,
                GenericOperationLogContent.builder()
                        .what(OAuth2Authorization.class)
                        .operation(new Read())
                        .state(new Start())
                        .detail(new Detail() {
                            private final String type = tokenType == null ? null : tokenType.getValue();
                        }).build(),
                null
        );
        if (tokenType == null) {
            lookupTokenTypes.addAll(Arrays.asList(OAuth2ParameterNames.STATE, OAuth2ParameterNames.CODE, OAuth2TokenType.ACCESS_TOKEN.getValue(), OidcParameterNames.ID_TOKEN, OAuth2TokenType.REFRESH_TOKEN.getValue()));
        } else {
            lookupTokenTypes.add(tokenType.getValue());
        }
        for (String type : lookupTokenTypes) {
            OAuth2Authorization auth2Authorization = valueSerializer.deserialize(cacheClient.executeScript(
                    //使用token反查授权id，再用授权id读取数据的脚本
                    this.queryScript,
                    1,
                    scriptKeySerializer.serialize(composeTokenKey(token, type)))
            );
            if (auth2Authorization != null) {
                return auth2Authorization;
            }
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TokenEssentials {
        /**
         * 默认是redis的空字符串
         */
        private String value = "";
        private long ttl = 0;
    }

    /**
     * @author ZhangHan
     * @date 2022/1/8 21:14
     */
    @Data
    @NoArgsConstructor
    private class OAuth2AuthorizationDetail implements Detail {
        private String authorization;

        public OAuth2AuthorizationDetail(OAuth2Authorization authorization) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JavaTimeModule javaTimeModule = new JavaTimeModule();
                javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
                mapper.registerModule(javaTimeModule);
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                this.authorization = mapper.writeValueAsString(authorization);
            } catch (JsonProcessingException e) {
                //omit exception
            }
        }
    }
}
