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
		SECTION, PRODUIT, TYPEBC, CAMPAGNE, GROUPE, VENDEUR, LIENGROUPE
	}

	private static final String DATE_PATTERN = "dd/MM/yyyy";

	private static final DateFormat df = new SimpleDateFormat(DATE_PATTERN);

	private static final String DATETIME_PATTERN = "dd/MM/yyyy HH:mm";

	public static final String DEFAULT_TIMEZONE = "America/Montreal";
	
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
	 * Converts an Instant to LocalDate (handles null)
	 * @param instant
	 * @return
	 */
	public static LocalDate convertToLocalDate(Instant instant) {
		if(instant == null) {
			return null;
		}
		return instant.atZone(ZoneId.of(DEFAULT_TIMEZONE)).toLocalDate();
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
		return instant.atZone(ZoneId.of(DEFAULT_TIMEZONE)).toLocalDateTime();
	}
	
	/**
	 * 
	 * @param instant
	 * @return
	 */
	public static String formatInstant(Instant instant) {
		LocalDateTime localDateTime = convertToLocalDateTime(instant);
		if(localDateTime != null) {
			return localDateTime.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
		} else {
			return "-";
		}
	}

	/**
	 * Format an instant to a date string
	 * @param instant
	 * @return
	 */
	public static String formatToDate(Instant instant) {
		LocalDate date = convertToLocalDate(instant);
		return formatLocalDate(date, DATE_PATTERN);
	}
	
	/**
	 * Parse a date of pattern yyyy-mm-dd in a String
	 * @param dateToParse date string
	 * @return the Date object
	 * @throws ParseException
	 */
	public static LocalDate parseDate(String dateToParse) throws ParseException {
		if(dateToParse == null || dateToParse.isBlank()) {
			return null;
		}
		return LocalDate.parse(dateToParse, DateTimeFormatter.ofPattern(DATE_PATTERN));
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
