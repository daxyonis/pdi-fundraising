package com.poivredesiles.fundraising.imports.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Campaign {
	
	private Long number;

	private String organizationNumber;
	
	private String organizationName;
	
	private String project;
	
	private String leaderNumber;
	
	private String leaderEmail;
	
	private String dueDate;
	
	private Long numTypeBC;
	
	private boolean blocked;
	
	private String closedDate;

	private Double percentProfit = Double.valueOf(50);

	private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\." +
			"[a-zA-Z0-9_+&*-]+)*@" +
			"(?:[a-zA-Z0-9-]+\\.)+[a-z" +
			"A-Z]{2,7}$";

	public boolean valid() {
		if(number == null) {
			return false;
		}

		// check if leaderEmail is of a valid email format
		if(leaderEmail != null && !leaderEmail.isBlank()) {
			// check it with regular expression
			if(!leaderEmail.matches(EMAIL_REGEX)) {
				return false;
			}
		}

		return true;		
	}
	
	public void setBlockedAsString(String blockedStr) {
		if(blockedStr == null || blockedStr.isBlank() || Integer.parseInt(blockedStr) != 1) {
			this.setBlocked(false);
		} else {
			this.setBlocked(true);
		}
	}
}
