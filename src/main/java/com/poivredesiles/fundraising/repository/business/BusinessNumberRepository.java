package com.poivredesiles.fundraising.repository.business;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.business.BusinessNumber;
import com.poivredesiles.fundraising.model.business.BusinessNumberTypeEnum;

public interface BusinessNumberRepository extends JpaRepository<BusinessNumber, BusinessNumberTypeEnum> {

	int countByType(BusinessNumberTypeEnum type);

}
