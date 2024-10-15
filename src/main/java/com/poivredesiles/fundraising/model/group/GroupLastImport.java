package com.poivredesiles.fundraising.model.group;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.time.Instant;

@Entity
@Immutable
@Subselect("SELECT id, last_modified_date as instant " +
		"FROM (" +
		"    SELECT id, last_modified_date FROM pdicampaign" +
		"    UNION" +
		"    SELECT id, last_modified_date FROM pdigroup" +
		"    UNION" +
		"    SELECT id, last_modified_date FROM pdiseller" +
		") as combined " +
		"ORDER BY last_modified_date DESC " +
		"LIMIT 1")
public class GroupLastImport {

	@Id 
	private String id;
	
	private Instant instant;

	public String getId() {
		return id;
	}

	public Instant getInstant() {
		return instant;
	}		
}
