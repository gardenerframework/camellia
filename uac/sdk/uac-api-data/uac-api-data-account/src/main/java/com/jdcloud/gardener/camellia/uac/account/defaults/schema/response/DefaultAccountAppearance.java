package com.jdcloud.gardener.camellia.uac.account.defaults.schema.response;

import com.jdcloud.gardener.camellia.uac.account.schema.response.AccountAppearanceTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author ZhangHan
 * @date 2022/11/8 11:26
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DefaultAccountAppearance extends AccountAppearanceTemplate {
    public DefaultAccountAppearance(String id, String surname, String givenName, Date dateOfBirth, Integer gender, String ethnicGroup, String email, String mobilePhoneNumber, String officeTelephoneNumber, String fax, boolean locked, boolean enabled, Date accountExpiryDate, String creator) {
        super(id, surname, givenName, dateOfBirth, gender, ethnicGroup, email, mobilePhoneNumber, officeTelephoneNumber, fax, locked, enabled, accountExpiryDate, creator);
    }
}
