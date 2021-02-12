package com.poivredesiles.fundraising.repository.group;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.group.PdiSeller;
import com.poivredesiles.fundraising.model.user.User;

public interface PdiSellerRepository extends JpaRepository<PdiSeller, Long> {

	Optional<PdiSeller> findOneByNumber(Long number);

	Optional<PdiSeller> findOneByMeOrBuyer(User user, User user2);

}
