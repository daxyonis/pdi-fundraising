package com.poivredesiles.fundraising.model.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="businessnumber")
@Data
public class BusinessNumber {

	@Id
	@Enumerated(EnumType.STRING)
	private BusinessNumberTypeEnum type;
	
	@Column(name="number")
	private Long number = 0L;
	
	public BusinessNumber() {}
	
	public BusinessNumber(BusinessNumberTypeEnum type) {
		this.type = type;
	}
}
