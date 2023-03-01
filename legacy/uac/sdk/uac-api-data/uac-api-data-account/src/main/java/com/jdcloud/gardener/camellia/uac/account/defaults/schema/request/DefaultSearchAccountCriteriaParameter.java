package com.jdcloud.gardener.camellia.uac.account.defaults.schema.request;

import com.jdcloud.gardener.camellia.uac.account.schema.request.SearchAccountCriteriaParameterTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author ZhangHan
 * @date 2022/11/8 11:25
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DefaultSearchAccountCriteriaParameter extends SearchAccountCriteriaParameterTemplate {
    public DefaultSearchAccountCriteriaParameter(Collection<String> must, Collection<String> should, String id, String username, String weChatOpenId, String alipayOpenId, String enterpriseWeChatOpenId, String dingTalkOpenId, String larkOpenId, String faceId, String name, String email, String mobilePhoneNumber) {
        super(must, should, id, username, weChatOpenId, alipayOpenId, enterpriseWeChatOpenId, dingTalkOpenId, larkOpenId, faceId, name, email, mobilePhoneNumber);
    }
}
