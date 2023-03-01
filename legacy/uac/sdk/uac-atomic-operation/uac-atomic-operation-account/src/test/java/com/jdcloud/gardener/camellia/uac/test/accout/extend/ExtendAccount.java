package com.jdcloud.gardener.camellia.uac.test.accout.extend;

import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author zhanghan30
 * @date 2022/9/20 18:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ExtendAccount extends AccountEntityTemplate implements TestTrait {
    private String test;

    public ExtendAccount(Date createdTime, Date lastUpdateTime, String id, String creator, String updater, String surname, String givenName, Integer gender, Date dateOfBirth, String ethnicGroup, String mobilePhoneNumber, String officeTelephoneNumber, String fax, String email, String test) {
        super(createdTime, lastUpdateTime, id, creator, updater, surname, givenName, gender, dateOfBirth, ethnicGroup, mobilePhoneNumber, officeTelephoneNumber, fax, email);
        this.test = test;
    }
}
