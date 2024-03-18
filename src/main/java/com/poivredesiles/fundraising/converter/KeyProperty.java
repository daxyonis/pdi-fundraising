package com.poivredesiles.fundraising.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.pando.crypto.nacl.SecretBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;


@Component
public class KeyProperty {

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
            var keyStore = KeyStore.getInstance("PKCS12");
            var pass = keystorePassword.toCharArray();
            keyStore.load(new FileInputStream(keystoreFilename), pass);
            var encKey =  keyStore.getKey("aes-key", pass);
            this.encryptionKey = SecretBox.key(encKey.getEncoded());
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
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
