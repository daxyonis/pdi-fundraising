package com.poivredesiles.fundraising.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.PdiGroup;

public interface PdiGroupRepository extends JpaRepository<PdiGroup, Long> {

	Optional<PdiGroup> findOneByNumber(Long number);

}
