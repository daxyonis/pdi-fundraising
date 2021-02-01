package com.poivredesiles.fundraising.imports.dto;

import java.util.Date;

import lombok.Data;

@Data
public class Campaign {
	
	private Long number;

	private String organizationNumber;
	
	private String organizationName;
	
	private String project;
	
	private String leaderNumber;
	
	private String leaderEmail;
	
	private Date dueDate;
	
	private Long numTypeBC;
	
	private boolean blocked;
	
	private Date closedDate;

	public boolean valid() {
		if(number == null) {
			return false;
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
