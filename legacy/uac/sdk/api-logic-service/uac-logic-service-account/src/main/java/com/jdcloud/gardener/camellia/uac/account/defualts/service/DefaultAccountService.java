package com.jdcloud.gardener.camellia.uac.account.defualts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.criteria.DefaultAccountCriteria;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.entity.DefaultAccountEntity;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.request.DefaultAuthenticateAccountParameter;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.request.DefaultCreateAccountParameter;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.request.DefaultSearchAccountCriteriaParameter;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.request.DefaultUpdateAccountParameter;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.response.DefaultAccountAppearance;
import com.jdcloud.gardener.camellia.uac.account.service.AccountServiceTemplate;
import com.jdcloud.gardener.camellia.uac.common.utils.CriteriaParameterUtils;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScanner;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.data.trait.sns.BioTraits;
import com.jdcloud.gardener.fragrans.data.trait.sns.SnsTraits;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZhangHan
 * @date 2022/11/8 11:35
 */
@ConditionalOnMissingBean(value = AccountServiceTemplate.class, ignored = DefaultAccountService.class)
public class DefaultAccountService extends AccountServiceTemplate<
        DefaultCreateAccountParameter,
        DefaultSearchAccountCriteriaParameter,
        DefaultUpdateAccountParameter,
        DefaultAuthenticateAccountParameter,
        DefaultAccountAppearance,
        DefaultAccountEntity,
        DefaultAccountCriteria
        > {
    /**
     * 搜索条件的字段和trait类的对照关系表
     */
    private static final Map<String, Class<?>> CRITERIA_FILED_TO_TRAIT_MAPPING = new ConcurrentHashMap<>();

    static {
        Collection<Class<?>> traits = Arrays.asList(
                ApiStandardDataTraits.Id.class,
                AccountTraits.Username.class,
                SnsTraits.WeChatOpenId.class,
                SnsTraits.AlipayOpenId.class,
                GenericTraits.Name.class,
                ContactTraits.MobilePhoneNumber.class,
                ContactTraits.Email.class,
                SnsTraits.DingTalkOpenId.class,
                SnsTraits.EnterpriseWeChatOpenId.class,
                SnsTraits.LarkOpenId.class,
                BioTraits.FaceId.class
        );
        //默认搜索参数实现的trait
        traits.forEach(
                clazz -> CRITERIA_FILED_TO_TRAIT_MAPPING.put(new FieldScanner().field(DefaultSearchAccountCriteriaParameter.class, clazz), clazz)
        );
    }

    public DefaultAccountService(
            ObjectMapper objectMapper
    ) {
        super(
                source -> objectMapper.convertValue(source, DefaultAccountAppearance.class),
                source -> CriteriaParameterUtils.createDomainCriteriaWrapper(
                        DefaultAccountCriteria.class,
                        source,
                        CRITERIA_FILED_TO_TRAIT_MAPPING,
                        objectMapper
                ),
                source -> {
                    DefaultAccountEntity account = objectMapper.convertValue(source, DefaultAccountEntity.class);
                    if (source.getMobilePhoneNumberProperty() != null) {
                        account.setMobilePhoneNumber(source.getMobilePhoneNumberProperty().getValue());
                    }
                    if (source.getEmailProperty() != null) {
                        account.setEmail(source.getEmailProperty().getValue());
                    }
                    return account;
                },
                source -> objectMapper.convertValue(source, DefaultAccountEntity.class),
                source -> objectMapper.convertValue(source, DefaultAccountCriteria.class)
        );
    }
}
