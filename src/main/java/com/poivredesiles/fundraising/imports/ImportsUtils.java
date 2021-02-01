package com.poivredesiles.fundraising.imports;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ImportsUtils {

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
}
