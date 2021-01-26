package com.poivredesiles.fundraising.jdbc;

public class JdbcUtils {

	public static String sanitize(String value) {
		if(value != null) {
			return value.strip();
		} else {
			return null;
		}
	}
}
