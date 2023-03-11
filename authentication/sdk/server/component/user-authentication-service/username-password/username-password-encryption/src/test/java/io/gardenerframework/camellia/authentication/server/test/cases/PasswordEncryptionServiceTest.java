package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.test.PasswordEncryptionServiceTestApplication;
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

@SpringBootTest(classes = PasswordEncryptionServiceTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PasswordEncryptionServiceTest {
    @Autowired
    private PasswordEncryptionService passwordEncryptionService;

    @Test
    public void smokeTest() throws Exception {
        String password = UUID.randomUUID().toString();
        PasswordEncryptionService.Key key = passwordEncryptionService.createKey();
        PublicKey rsa = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key.getKey())));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, rsa);
        Assertions.assertEquals(
                password,
                passwordEncryptionService.decrypt(key.getId(), Base64.getEncoder().encodeToString(
                        cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)))));

    }
}
