package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.security.encryption.EncryptionService;
import io.gardenerframework.camellia.authentication.server.security.encryption.schema.EncryptionKey;
import io.gardenerframework.camellia.authentication.server.test.RsaEncryptionServiceTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.UUID;

@SpringBootTest(classes = RsaEncryptionServiceTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RsaEncryptionServiceTest {
    @Autowired
    private EncryptionService encryptionService;

    @Test
    public void smokeTest() throws Exception {
        String password = UUID.randomUUID().toString();
        EncryptionKey key = encryptionService.createKey(Duration.ofSeconds(30));
        Assertions.assertEquals(
                password,
                new String(encryptionService.decrypt(key.getId(),
                        encryptionService.encrypt(key.getId(), password.getBytes())
                )));

    }
}
