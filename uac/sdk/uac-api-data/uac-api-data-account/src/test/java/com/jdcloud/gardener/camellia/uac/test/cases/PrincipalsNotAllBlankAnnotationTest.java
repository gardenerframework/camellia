package com.jdcloud.gardener.camellia.uac.test.cases;

import com.jdcloud.gardener.camellia.uac.account.defaults.schema.request.DefaultAuthenticateAccountParameter;
import com.jdcloud.gardener.camellia.uac.test.UacApiDataTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/11/9 12:16
 */
@SpringBootTest(classes = UacApiDataTestApplication.class)
public class PrincipalsNotAllBlankAnnotationTest {
    @Autowired
    private Validator validator;

    @Test
    public void smokeTest() {
        DefaultAuthenticateAccountParameter parameter = new DefaultAuthenticateAccountParameter();
        parameter.setPassword(UUID.randomUUID().toString());
        Set<ConstraintViolation<DefaultAuthenticateAccountParameter>> validate = validator.validate(parameter);
        Assertions.assertEquals(1, validate.size());
        parameter.setUsername(UUID.randomUUID().toString());
        validate = validator.validate(parameter);
        Assertions.assertEquals(0, validate.size());
    }
}
