package io.gardenerframework.camellia.authentication.server.security.encryption;

import io.gardenerframework.camellia.authentication.server.security.encryption.schema.EncryptionKey;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class RsaEncryptionService implements EncryptionService {
    private final String[] NAMESPACE = new String[]{
            "authentication",
            "server",
            "component",
            "security",
            "encryption",
            "rsa"
    };

    private final String SUFFIX = "key";

    private final BasicCacheManager<String> cacheManager;

    public RsaEncryptionService(CacheClient client) {
        this.cacheManager = new BasicCacheManager<String>(client) {
        };
    }

    @Override
    public EncryptionKey createKey(@NonNull Duration ttl) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = generator.generateKeyPair();
        String rawPublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String rawPrivateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String keyId = UUID.randomUUID().toString();
        cacheManager.set(buildNamespace(KeyType.PRIVATE), keyId, SUFFIX, rawPrivateKey, ttl);
        cacheManager.set(buildNamespace(KeyType.PUBLIC), keyId, SUFFIX, rawPublicKey, ttl);
        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()));
        return EncryptionKey.builder()
                .id(keyId)
                .key(Base64.getEncoder().encodeToString(publicKey.getEncoded()))
                .expiryTime(Date.from(Instant.now().plus(ttl)))
                .build();
    }

    private String getSavedKey(@NonNull String id, @NonNull KeyType keyType) throws InvalidKeyException, Exception {
        String keySaved = cacheManager.get(buildNamespace(keyType), id, SUFFIX);
        if (keySaved == null) {
            throw new InvalidKeyException(id);
        }
        return keySaved;
    }

    @Override
    public byte[] encrypt(@NonNull String id, @NonNull byte[] content) throws InvalidKeyException, Exception {
        String publicKeySaved = getSavedKey(id, KeyType.PUBLIC);
        byte[] decode = Base64.getDecoder().decode(publicKeySaved);
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decode));
        Cipher rasCipher = Cipher.getInstance("RSA");
        rasCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return rasCipher.doFinal(content);

    }

    @Override
    public byte[] decrypt(@NonNull String id, @NonNull byte[] cipher) throws InvalidKeyException, Exception {
        String privateKeySaved = getSavedKey(id, KeyType.PRIVATE);
        byte[] decode = Base64.getDecoder().decode(privateKeySaved);
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decode));
        Cipher rasCipher = Cipher.getInstance("RSA");
        rasCipher.init(Cipher.DECRYPT_MODE, privateKey);
        return rasCipher.doFinal(cipher);

    }

    private String[] buildNamespace(@NonNull KeyType keyType) {
        return ArrayUtils.add(NAMESPACE, keyType.toString());
    }

    private enum KeyType {
        PUBLIC,
        PRIVATE
    }
}
