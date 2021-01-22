package com.poivredesiles.fundraising.service.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class MapperUtils {

	private DateTimeFormatter formatter = 
			DateTimeFormatter.ofLocalizedDate( FormatStyle.SHORT )
							 .withLocale( Locale.CANADA )
							 .withZone( ZoneId.systemDefault() );
	
	
	@Named("instantToString")
	public String instantToString(Instant instant) {
		return formatter.format(instant);
	}
}
