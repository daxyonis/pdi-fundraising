package com.poivredesiles.fundraising.model.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.time.Instant;


@Entity
@Immutable
@Subselect("SELECT id, last_modified_date as instant " +
		"FROM (" +
		"    SELECT id, last_modified_date FROM pdiproduct" +
		"    UNION" +
		"    SELECT id, last_modified_date FROM pdicategory    " +
		") as combined " +
		"ORDER BY last_modified_date DESC " +
		"LIMIT 1")
public class ProductLastImport {

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
