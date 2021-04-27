package com.poivredesiles.fundraising.resource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class ResourceUtils {

	private HashMap<Province,String> provincesMapEn = new LinkedHashMap<>();
	private HashMap<Province,String> provincesMapFr = new LinkedHashMap<>();
	
	@Autowired
	private MessageSource messageSource;
	
	public HashMap<Province,String> getProvincesMap(Locale locale) {
		if(provincesMapEn.isEmpty() || provincesMapFr.isEmpty()) {
			initProvincesMap();
		}
		if(locale.getLanguage().toUpperCase().contains("FR")) {
			return provincesMapFr;
		} else {
			return provincesMapEn;
		}
	}

	private void initProvincesMap() {
		for(Province province : Province.values()) {
			provincesMapFr.put(province, messageSource.getMessage("province." + province.name(), null, Locale.FRENCH));
			provincesMapEn.put(province, messageSource.getMessage("province." + province.name(), null, Locale.ENGLISH));
		}		
	}
}
