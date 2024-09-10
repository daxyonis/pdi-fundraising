package com.poivredesiles.fundraising.bootstrap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.poivredesiles.fundraising.model.notification.NotificationSettings;
import com.poivredesiles.fundraising.repository.notification.NotificationSettingsRepository;
import com.poivredesiles.fundraising.service.EncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.poivredesiles.fundraising.model.business.BusinessNumber;
import com.poivredesiles.fundraising.model.business.BusinessNumberTypeEnum;
import com.poivredesiles.fundraising.model.user.Role;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.repository.business.BusinessNumberRepository;
import com.poivredesiles.fundraising.repository.user.RoleRepository;
import com.poivredesiles.fundraising.repository.user.UserRepository;

@Component
public class AppStartup implements CommandLineRunner {
	
	private final Logger log = LoggerFactory.getLogger(AppStartup.class);

	@Value("#{${application.admin.usernames}}")
	private List<String> adminUsernames;

	@Value("#{${application.admin.passwords}}")
	private List<String> adminPasswords;

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;
	
	private final BusinessNumberRepository businessNumberRepository;

	private final EncryptionService encryptionService;

	private final NotificationSettingsRepository notificationSettingsRepository;

	public AppStartup(UserRepository userRepository,
					  RoleRepository roleRepository,
					  BusinessNumberRepository businessNumberRepository,
					  EncryptionService encryptionService,
					  NotificationSettingsRepository notificationSettingsRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.businessNumberRepository = businessNumberRepository;
		this.encryptionService = encryptionService;
		this.notificationSettingsRepository = notificationSettingsRepository;
	}

	@Override
	public void run(String... args) throws Exception {

		createRoles();

		createAdminUsers();
		
		createBusinessNumbers();

		createNotificationSettings();

		runActions();

	}

	private void runActions() {

		encryptionService.batchEncrypt();

	}




	private void createAdminUsers() {
		// Create ADMIN users
		Set<Role> roles = new HashSet<>();
		roles.add(new Role(RoleEnum.ROLE_ADMIN));
		int index = 0;
		for (String username : adminUsernames) {
			if (userRepository.countByUsername(username) < 1) {
				log.info("Creating user {}", username);
				User user = new User();
				user.setRoles(roles);
				user.setUsername(username);
				user.setPassword(adminPasswords.get(index++));
				user.setLanguage("FR");
				user.setCreatedBy("system");
				userRepository.save(user);
			}
		}
	}

	private void createRoles() {
		long numRoles = roleRepository.count();
		if (numRoles < (long) RoleEnum.values().length) {
			for (RoleEnum role : RoleEnum.values()) {
				if (roleRepository.findById(role.name()).isEmpty()) {
					log.info("Creating role {}", role.name());
					roleRepository.save(new Role(role));
				}
			}
			numRoles = roleRepository.count();
			assert (numRoles == (long) RoleEnum.values().length);
		}
	}
	
	private void createBusinessNumbers() {
		for(BusinessNumberTypeEnum type : BusinessNumberTypeEnum.values()) {
			if(businessNumberRepository.countByType(type) == 0) {
				businessNumberRepository.save(new BusinessNumber(type));
			}
		}
		long numBusinessNums = businessNumberRepository.count();
		assert(numBusinessNums == (long) BusinessNumberTypeEnum.values().length);
	}

	private void createNotificationSettings() {
		if(notificationSettingsRepository.count() == 0) {
			notificationSettingsRepository.save(new NotificationSettings());
		}
	}
}
