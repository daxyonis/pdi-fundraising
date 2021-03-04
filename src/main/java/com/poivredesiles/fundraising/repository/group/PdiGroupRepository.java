package com.poivredesiles.fundraising.repository.group;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.group.PdiGroup;

public interface PdiGroupRepository extends JpaRepository<PdiGroup, Long> {

	Optional<PdiGroup> findOneByNumber(Long number);

	Set<PdiGroup> findByGroupLeader_Me_id(Long userId);
}
