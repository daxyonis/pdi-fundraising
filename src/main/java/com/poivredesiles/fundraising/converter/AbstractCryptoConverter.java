package com.poivredesiles.fundraising.converter;

import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import software.pando.crypto.nacl.SecretBox;

import javax.persistence.AttributeConverter;
import java.security.Key;


import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public abstract class AbstractCryptoConverter<T> implements AttributeConverter<T, String> {

    private final KeyProperty keyProperty;
    private ApplicationProperties applicationProperties;

    public AbstractCryptoConverter(ApplicationProperties applicationProperties, KeyProperty keyProperty) {
        this.applicationProperties = applicationProperties;
        this.keyProperty = keyProperty;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        boolean encyptionEnabled = applicationProperties.isEncrypted() || applicationProperties.getAction().isEncrypt();
        if (encyptionEnabled && !keyProperty.isEmpty() && isNotNullOrEmpty(attribute)) {
            return encrypt(attribute);
        }
        return entityAttributeToString(attribute);
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (applicationProperties.isEncrypted() && !keyProperty.isEmpty() && isNotEmpty(dbData)) {
            return decrypt(dbData);
        }
        return stringToEntityAttribute(dbData);
    }

    abstract boolean isNotNullOrEmpty(T attribute);

    abstract T stringToEntityAttribute(String dbData);

    abstract String entityAttributeToString(T attribute);


    private String encrypt(T attribute) {
        String stringToEncrypt = entityAttributeToString(attribute);
        String encryptedBytes = SecretBox.encrypt(keyProperty.getEncryptionKey(), stringToEncrypt).toString();
        return encryptedBytes;
    }

    private T decrypt(String dbData) {
        var box = SecretBox.fromString(dbData);
        String decryptedString = box.decryptToString(keyProperty.getEncryptionKey());
        return stringToEntityAttribute(decryptedString);
    }
}