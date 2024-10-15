package com.poivredesiles.fundraising.model.business;

import jakarta.persistence.*;
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
