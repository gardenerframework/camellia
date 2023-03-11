package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.security.encryption.EncryptionService;
import io.gardenerframework.camellia.authentication.server.security.encryption.schema.EncryptionKey;
import io.gardenerframework.camellia.authentication.server.test.RsaEncryptionServiceTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@SpringBootTest(classes = RsaEncryptionServiceTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RsaEncryptionServiceTest {
    @Autowired
    private EncryptionService encryptionService;

    @Test
    public void smokeTest() throws Exception {
        String password = UUID.randomUUID().toString();
        EncryptionKey key = encryptionService.createKey();
        PublicKey rsa = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key.getKey())));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, rsa);
        Assertions.assertEquals(
                password,
                new String(encryptionService.decrypt(key.getId(),
                        cipher.doFinal(password.getBytes(StandardCharsets.UTF_8))))
        );

    }
}
