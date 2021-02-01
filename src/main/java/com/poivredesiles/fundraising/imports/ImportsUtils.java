package com.poivredesiles.fundraising.imports;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ImportsUtils {
	
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Stip blank spaces from a string (handles null)
	 * @param value
	 * @return
	 */
	public static String sanitize(String value) {
		if(value != null) {
			return value.strip();
		} else {
			return null;
		}
	}
	
	/**
	 * Converts a Date to LocalDate (handles null)
	 * @param dateToConvert
	 * @return
	 */
	public static LocalDate convertToLocalDate(Date dateToConvert) {
		if(dateToConvert == null) {
			return null;
		}
		if(dateToConvert instanceof java.sql.Date) {
			return ((java.sql.Date) dateToConvert).toLocalDate();
		} else {
			return dateToConvert.toInstant()
				      .atZone(ZoneId.systemDefault())
				      .toLocalDate();
		}
	}
	
	/**
	 * Parse a date of pattern yyyy-mm-dd in a String
	 * @param dateToParse date string
	 * @return the Date object
	 * @throws ParseException
	 */
	public static Date parseDate(String dateToParse, DateFormat myDateFormat) throws ParseException {		
		if(dateToParse == null || dateToParse.isBlank()) {
			return null;
		}
		if(myDateFormat == null) {
			return df.parse(dateToParse);
		} else {
			return myDateFormat.parse(dateToParse);
		}
	}
}
