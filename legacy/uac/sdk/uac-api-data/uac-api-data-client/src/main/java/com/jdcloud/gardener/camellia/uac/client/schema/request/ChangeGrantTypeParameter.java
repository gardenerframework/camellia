package com.jdcloud.gardener.camellia.uac.client.schema.request;

import com.jdcloud.gardener.camellia.uac.client.schema.trait.GrantType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/14 17:00
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChangeGrantTypeParameter implements GrantType {
    private Collection<String> grantType;
}
