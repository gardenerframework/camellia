package com.jdcloud.gardener.camellia.uac.test.client.cases;

import com.jdcloud.gardener.camellia.uac.client.atomic.ClientAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.client.defaults.schema.criteria.DefaultClientCriteria;
import com.jdcloud.gardener.camellia.uac.client.defaults.schema.entity.DefaultClientEntity;
import com.jdcloud.gardener.camellia.uac.common.atomic.PasswordEncoder;
import com.jdcloud.gardener.camellia.uac.common.exception.client.IncorrectPasswordException;
import com.jdcloud.gardener.camellia.uac.test.UacAtomicOperationTestApplication;
import com.jdcloud.gardener.fragrans.data.schema.query.GenericQueryResult;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/11/9 13:18
 */
@SpringBootTest(classes = UacAtomicOperationTestApplication.class)
public class ClientAtomicOperationTest {
    @Autowired
    public PasswordEncoder<? super DefaultClientEntity> passwordEncoder;
    @Autowired
    private ClientAtomicOperationTemplate<DefaultClientEntity, DefaultClientCriteria> clientAtomicOperation;

    @Test
    public void smokeTest() {
        String password = UUID.randomUUID().toString();
        DefaultClientEntity client = new DefaultClientEntity();
        client.setId(UUID.randomUUID().toString());
        client.setPassword(password);
        client.setName(UUID.randomUUID().toString());
        client.setEnabled(true);
        client.setGrantType(Arrays.asList("authorization_code", "client_credentials"));
        client.setScope(Collections.singletonList(UUID.randomUUID().toString()));
        clientAtomicOperation.createClient(
                client,
                passwordEncoder
        );
        //创建完毕后读取
        DefaultClientEntity clientFromDatabase = clientAtomicOperation.readClient(client.getId(), false);
        Assertions.assertEquals(client.getName(), clientFromDatabase.getName());
        Assertions.assertEquals(client.getGrantType(), clientFromDatabase.getGrantType());
        Assertions.assertEquals(client.getScope(), clientFromDatabase.getScope());
        Assertions.assertTrue(clientFromDatabase.isEnabled());
        //认证
        clientAtomicOperation.<DefaultClientEntity>authenticate(client.getId(), password, passwordEncoder);
        //给一个错误的密码
        Assertions.assertThrowsExactly(
                IncorrectPasswordException.class,
                () -> clientAtomicOperation.<DefaultClientEntity>authenticate(client.getId(), UUID.randomUUID().toString(), passwordEncoder)
        );
        //搜索一下
        GenericQueryResult<DefaultClientEntity> queryResult = clientAtomicOperation.searchClient(
                DefaultClientCriteria.builder().name(client.getName()).build(),
                Collections.singletonList(GenericTraits.Name.class),
                null,
                1, 100
        );
        Assertions.assertEquals(1, queryResult.getContents().size());
        Assertions.assertEquals(client.getId(), queryResult.getContents().stream().findFirst().get().getId());
        //更新一下
        client.setDescription(UUID.randomUUID().toString());
        clientAtomicOperation.updateClient(client);
        clientFromDatabase = clientAtomicOperation.readClient(client.getId(), false);
        Assertions.assertEquals(client.getDescription(), clientFromDatabase.getDescription());
        //更新scope
        client.setScope(Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        clientAtomicOperation.changeClientScope(client.getId(), client.getScope());
        clientFromDatabase = clientAtomicOperation.readClient(client.getId(), false);
        Assertions.assertEquals(client.getScope(), clientFromDatabase.getScope());
        //变化grang type
        client.setGrantType(null);
        clientAtomicOperation.changeClientGrantType(client.getId(), client.getGrantType());
        clientFromDatabase = clientAtomicOperation.readClient(client.getId(), false);
        Assertions.assertNull(clientFromDatabase.getGrantType());
        //回调地址
        client.setRedirectUri(Collections.singletonList(UUID.randomUUID().toString()));
        clientAtomicOperation.changeClientRedirectUri(client.getId(), client.getRedirectUri());
        clientFromDatabase = clientAtomicOperation.readClient(client.getId(), false);
        Assertions.assertEquals(client.getRedirectUri(), clientFromDatabase.getRedirectUri());
        //更新是否自动批准
        client.setRequireConsent(true);
        clientAtomicOperation.changeClientRequireConsentFlag(client.getId(), client.isRequireConsent());
        clientFromDatabase = clientAtomicOperation.readClient(client.getId(), false);
        Assertions.assertTrue(clientFromDatabase.isRequireConsent());
        client.setRequireConsent(false);
        Assertions.assertTrue(clientAtomicOperation.changeClientRequireConsentFlag(client.getId(), client.isRequireConsent()));
        clientFromDatabase = clientAtomicOperation.readClient(client.getId(), false);
        Assertions.assertFalse(clientFromDatabase.isRequireConsent());
        //执行认证
        clientAtomicOperation.authenticate(
                client.getId(),
                password,
                passwordEncoder
        );
        //给一个错误的密码
        Assertions.assertThrowsExactly(
                IncorrectPasswordException.class,
                () -> clientAtomicOperation.authenticate(
                        client.getId(),
                        UUID.randomUUID().toString(),
                        passwordEncoder
                )
        );
    }
}
