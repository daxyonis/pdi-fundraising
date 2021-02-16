package com.poivredesiles.fundraising.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.model.business.BusinessNumber;
import com.poivredesiles.fundraising.model.business.BusinessNumberTypeEnum;
import com.poivredesiles.fundraising.repository.business.BusinessNumberRepository;

@Service
@Transactional
public class BusinessNumberService {

	@Autowired
	private BusinessNumberRepository businessNumberRepository; 
	
	/**
	 * Get the next number for a business number type
	 * @param type the number type
	 * @return the next number
	 */
	public Long getNextNumber(BusinessNumberTypeEnum type) {
		BusinessNumber businessNumber = businessNumberRepository.findById(type).orElseThrow();
		businessNumber.setNumber(businessNumber.getNumber() + 1L);		
		return businessNumber.getNumber();		
	}
}
