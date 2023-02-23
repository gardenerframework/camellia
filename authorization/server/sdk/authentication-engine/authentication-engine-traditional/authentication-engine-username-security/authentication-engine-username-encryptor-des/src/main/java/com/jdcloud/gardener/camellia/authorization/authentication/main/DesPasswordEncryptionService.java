package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.EncryptionAlgorithm;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@EncryptionAlgorithm("des")
public class DesPasswordEncryptionService extends Base64PasswordEncryptionService {

    @Override
    protected String decryptInternally(String key, byte[] password) throws Exception {
        byte[] keyBlock = Base64.getDecoder().decode(key);
        SecretKey originalKey = new SecretKeySpec(keyBlock, 0, keyBlock.length, "DES");
        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        desCipher.init(Cipher.DECRYPT_MODE, originalKey);
        return new String(desCipher.doFinal(password), StandardCharsets.UTF_8);
    }

    @Override
    public String generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] encoded = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(encoded);
    }
}
