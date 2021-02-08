package com.poivredesiles.fundraising.model.product;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;


@Entity
@Immutable
@Subselect("SELECT 'max' as id, GREATEST(MAX(p.last_modified_date), MAX(c.last_modified_date)) AS instant FROM pdiproduct p, pdicategory c")
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
