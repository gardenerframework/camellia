package io.gardenerframework.camellia.authentication.server.security.encryption;

import io.gardenerframework.camellia.authentication.server.security.encryption.schema.EncryptionKey;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;


@ConditionalOnClass(BasicCacheManager.class)
@ConditionalOnMissingBean(value = EncryptionService.class, ignored = RsaEncryptionService.class)
public class RsaEncryptionService implements EncryptionService {
    private final String[] NAMESPACE = new String[]{
            "authentication",
            "server",
            "component",
            "security",
            "encryption"
    };

    private final String SUFFIX = "key";

    private final BasicCacheManager<String> cacheManager;

    public RsaEncryptionService(CacheClient client) {
        this.cacheManager = new BasicCacheManager<String>(client) {
        };
    }

    @Override
    public EncryptionKey createKey() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = generator.generateKeyPair();
        String rawPrivateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String keyId = UUID.randomUUID().toString();
        Duration ttl = Duration.ofSeconds(30);
        cacheManager.set(NAMESPACE, keyId, SUFFIX, rawPrivateKey, ttl);
        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()));
        return EncryptionKey.builder()
                .id(keyId)
                .key(Base64.getEncoder().encodeToString(publicKey.getEncoded()))
                .expiryTime(Date.from(Instant.now().plus(ttl)))
                .build();
    }

    @Override
    public byte[] decrypt(@NonNull String id, @NonNull byte[] cipher) throws Exception {
        String privateKeySaved = cacheManager.get(NAMESPACE, id, SUFFIX);
        if (privateKeySaved == null) {
            throw new InvalidKeyException(id);
        } else {
            //保证秘钥的一次性有效
            cacheManager.delete(NAMESPACE, id, SUFFIX);
            byte[] decode = Base64.getDecoder().decode(privateKeySaved);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decode));
            Cipher rasCipher = Cipher.getInstance("RSA");
            rasCipher.init(Cipher.DECRYPT_MODE, privateKey);
            return rasCipher.doFinal(cipher);
        }
    }
}
