package com.poivredesiles.fundraising.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserUnlockService {

	private final UserRepository userRepository;
	
	@Value("${application.account.lockedDuration}")
	private Integer lockedDuration;
	
	@Scheduled(fixedRate = 60000)
	public void unlockAccounts() {
		log.debug("Running unlock accounts");
		
		List<User> lockedUsers = userRepository
				.findAllByLockedAndLastModifiedDateBefore(true, 
						LocalDateTime.now().minusMinutes(lockedDuration).atZone(ZoneId.systemDefault()).toInstant());
		
		if(lockedUsers.size() > 0) {
			log.debug("Found {} locked accounts, unlocking", lockedUsers.size());
			for(User user : lockedUsers) {
				user.setLocked(false);			
			}
			
			userRepository.saveAll(lockedUsers);
		}
	}
}
