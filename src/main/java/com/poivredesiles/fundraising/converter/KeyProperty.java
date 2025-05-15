package com.poivredesiles.fundraising.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import software.pando.crypto.nacl.SecretBox;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;


@Component
public class KeyProperty {

    private final Logger log = LoggerFactory.getLogger(KeyProperty.class);
    @Value("${keystore.password}")
    private String keystorePassword;

    @Value("${keystore.filename}")
    private String keystoreFilename;

    private Key encryptionKey;

    public Key getEncryptionKey() {
        if (encryptionKey == null) {
            loadEncryptionKey();
        }
        return encryptionKey;
    }

    public void loadEncryptionKey() {
        try {
            log.info("Loading from store: {}", keystoreFilename);
            var keyStore = KeyStore.getInstance("PKCS12");
            var pass = keystorePassword.toCharArray();
            InputStream inputStream = new ClassPathResource(keystoreFilename).getInputStream();
            keyStore.load(inputStream, pass);
            var encKey =  keyStore.getKey("aes-key", pass);
            this.encryptionKey = SecretBox.key(encKey.getEncoded());
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error loading encryption key");
        }
    }

    public boolean isEmpty() {
        return getEncryptionKey() == null || getEncryptionKey().getEncoded().length == 0;
    }

    public String setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
        return keystorePassword;
    }

    public String setKeystoreFilename(String keystoreFilename) {
        this.keystoreFilename = keystoreFilename;
        return keystoreFilename;
    }
}
