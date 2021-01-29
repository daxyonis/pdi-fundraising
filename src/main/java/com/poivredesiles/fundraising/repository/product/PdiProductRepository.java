package com.poivredesiles.fundraising.repository.product;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.poivredesiles.fundraising.model.product.PdiProduct;

/**
 * Spring Data  repository for the PdiProduct entity.
 */
@Repository
public interface PdiProductRepository extends JpaRepository<PdiProduct, Long> {

	Optional<PdiProduct> findByProductNumber(String number);

	@Query("select p from PdiProduct p where p.productNumber in (?1)")	
	Set<PdiProduct> findAllFromProductNumberList(List<String> productNumberList);
}
