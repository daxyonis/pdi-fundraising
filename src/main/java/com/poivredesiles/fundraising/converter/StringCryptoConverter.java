package com.poivredesiles.fundraising.converter;

import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import org.springframework.stereotype.Component;

import javax.persistence.Converter;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Converter
@Component
public class StringCryptoConverter extends AbstractCryptoConverter<String> {

    public StringCryptoConverter(CipherInitializer cipherInitializer, ApplicationProperties applicationProperties) {
        super(cipherInitializer, applicationProperties);
    }

    @Override
    boolean isNotNullOrEmpty(String attribute) {
        return isNotEmpty(attribute);
    }

    @Override
    String stringToEntityAttribute(String dbData) {
        return dbData;
    }

    @Override
    String entityAttributeToString(String attribute) {
        return attribute;
    }
}
