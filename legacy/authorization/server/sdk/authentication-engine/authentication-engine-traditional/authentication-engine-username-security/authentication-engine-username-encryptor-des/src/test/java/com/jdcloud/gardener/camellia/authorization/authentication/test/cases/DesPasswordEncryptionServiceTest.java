package com.jdcloud.gardener.camellia.authorization.authentication.test.cases;

import com.jdcloud.gardener.camellia.authorization.authentication.configuration.DesPasswordEncryptionServiceConfiguration;
import com.jdcloud.gardener.camellia.authorization.authentication.main.DesPasswordEncryptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SpringBootTest(classes = DesPasswordEncryptionServiceConfiguration.class)
public class DesPasswordEncryptionServiceTest {
    @Autowired
    private DesPasswordEncryptionService desPasswordEncryptionService;

    @Test
    public void smokeTest() throws Exception {
        String key = desPasswordEncryptionService.generateKey();
        byte[] keyNotBase64 = Base64.getDecoder().decode(key);
        SecretKey originalKey = new SecretKeySpec(keyNotBase64, 0, keyNotBase64.length, "DES");
        String text = "hello how are you";
        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        desCipher.init(Cipher.ENCRYPT_MODE, originalKey);
        byte[] textEncrypted = desCipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        String decrypt = desPasswordEncryptionService.decrypt(key, Base64.getEncoder().encodeToString(textEncrypted));
        Assertions.assertEquals(text, decrypt);
    }
}
