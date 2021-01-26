package com.poivredesiles.fundraising.bootstrap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.poivredesiles.fundraising.model.user.Role;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.repository.user.RoleRepository;
import com.poivredesiles.fundraising.repository.user.UserRepository;

@Component
public class AppStartup implements CommandLineRunner {
	
	private final Logger log = LoggerFactory.getLogger(AppStartup.class);

	@Value("#{${application.admin.usernames}}")
	private List<String> adminUsernames;

	@Value("#{${application.admin.passwords}}")
	private List<String> adminPasswords;

	private UserRepository userRepository;

	private RoleRepository roleRepository;

	public AppStartup(UserRepository userRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	@Override
	public void run(String... args) throws Exception {

		createRoles();

		createAdminUsers();

	}

	private void createAdminUsers() {
		// Create ADMIN users
		Set<Role> roles = new HashSet<>();
		roles.add(new Role(RoleEnum.ADMIN));
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
}
