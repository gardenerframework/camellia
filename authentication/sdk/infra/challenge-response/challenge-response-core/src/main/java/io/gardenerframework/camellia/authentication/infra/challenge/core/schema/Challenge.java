package io.gardenerframework.camellia.authentication.infra.challenge.core.schema;

import io.gardenerframework.camellia.authentication.infra.common.Version;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 基准的挑战
 *
 * @author ZhangHan
 * @date 2022/5/15 18:43
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class Challenge implements
        GenericTraits.IdentifierTraits.Id<String>,
        GenericTraits.GroupingTraits.Type<String>,
        Serializable {
    private static final long serialVersionUID = Version.current;
    /**
     * 挑战id
     */
    @NonNull
    private String id;
    /**
     * 挑战的类型，比如是短信验证码，还是动态令牌
     */
    @NonNull
    private String type;
    /**
     * 挑战的过期时间，超过这个时间即认为挑战无效
     */
    @NonNull
    private Date expiryTime;
    /**
     * 元数据，也就是额外信息
     */
    @Nullable
    private Map<String, String> metadata;
}
