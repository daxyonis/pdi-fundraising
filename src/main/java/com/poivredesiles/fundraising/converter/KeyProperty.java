package com.poivredesiles.fundraising.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
public class KeyProperty {
    public static String DATABASE_ENCRYPTION_KEY;

    @Value("${application.secret}")
    public void setDatabase(String databaseEncryptionKey) {
        DATABASE_ENCRYPTION_KEY = databaseEncryptionKey;
    }

}
