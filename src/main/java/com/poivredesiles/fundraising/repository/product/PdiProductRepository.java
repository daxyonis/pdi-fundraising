package com.poivredesiles.fundraising.repository.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poivredesiles.fundraising.model.PdiProduct;

/**
 * Spring Data  repository for the PdiProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PdiProductRepository extends JpaRepository<PdiProduct, Long> {

	Optional<PdiProduct> findByProductNumber(String number);
}
