package com.poivredesiles.fundraising.model.group;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Subselect("SELECT 'max' as id, GREATEST(MAX(c.last_modified_date), MAX(g.last_modified_date), MAX(s.last_modified_date)) AS instant FROM pdicampaign c, pdigroup g, pdiseller s")
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
