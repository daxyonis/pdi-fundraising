package com.poivredesiles.fundraising.repository.group;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.group.PdiSeller;

public interface PdiSellerRepository extends JpaRepository<PdiSeller, Long> {

	Optional<PdiSeller> findOneByNumber(Long number);

}
