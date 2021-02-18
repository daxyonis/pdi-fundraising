package com.poivredesiles.fundraising.imports;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class ImportsUtils {
	
	public static enum DataTypeEnum {
		SECTION, PRODUCT, ORDERTYPE, CAMPAIGN, GROUP, SELLER, GROUPLINK
	}
	
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final NumberFormat currencyNf = NumberFormat.getCurrencyInstance(Locale.CANADA_FRENCH);

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
	 * Converts an Instant to LocalDate (handles null)
	 * @param instant
	 * @return
	 */
	public static LocalDate convertToLocalDate(Instant instant) {
		if(instant == null) {
			return null;
		}
		return instant.atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	/**
	 * Converts an Instant to LocalDateTime (handles null)
	 * @param instant
	 * @return
	 */
	public static LocalDateTime convertToLocalDateTime(Instant instant) {
		if(instant == null) {
			return null;
		}
		return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	/**
	 * 
	 * @param instant
	 * @return
	 */
	public static String formatInstant(Instant instant) {
		LocalDateTime localDateTime = convertToLocalDateTime(instant);
		if(localDateTime != null) {
			return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		} else {
			return "-";
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

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String formatLocalDate(LocalDate date, String pattern) {
		if(date != null) {
			return date.format(DateTimeFormatter.ofPattern(pattern));
		} else {
			return "";
		}	
	}
	
	/**
	 * Format an amount to currency string
	 * @param amount
	 * @return
	 */
	public static String formatCurrency(BigDecimal amount) {  
    	currencyNf.setMaximumFractionDigits(2);
    	return currencyNf.format(amount); 
    }
}
