package com.poivredesiles.fundraising.converter;

import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Converter
@Component
public class StringCryptoConverter extends AbstractCryptoConverter<String> {

    public StringCryptoConverter(ApplicationProperties applicationProperties, KeyProperty keyProperty) {
        super(applicationProperties, keyProperty);
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
