package com.poivredesiles.fundraising.repository.product;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poivredesiles.fundraising.model.product.PdiCategory;

/**
 * Spring Data  repository for the PdiCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PdiCategoryRepository extends JpaRepository<PdiCategory, Long> {

	int countByNumber(Long number);

	PdiCategory findByNumber(Long number);
}
