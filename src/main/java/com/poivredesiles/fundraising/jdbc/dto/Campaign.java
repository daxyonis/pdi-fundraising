package com.poivredesiles.fundraising.jdbc.dto;

import java.util.Date;

import lombok.Data;

@Data
public class Campaign {

	private String organizationNumber;
	
	private String organizationName;
	
	private String project;
	
	private String leaderNumber;
	
	private String leaderEmail;
	
	private Date dueDate;
	
	private Long numTypeBC;
	
	private String blocked;
	
	private Date closedDate;
}
