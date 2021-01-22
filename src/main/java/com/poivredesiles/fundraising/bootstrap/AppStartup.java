package com.poivredesiles.fundraising.bootstrap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.poivredesiles.fundraising.model.Role;
import com.poivredesiles.fundraising.model.RoleEnum;
import com.poivredesiles.fundraising.model.User;
import com.poivredesiles.fundraising.repository.RoleRepository;
import com.poivredesiles.fundraising.repository.UserRepository;

@Component
public class AppStartup implements CommandLineRunner {

	@Value("#{${application.admin.usernames}}")
	private List<String> adminUsernames;

	@Value("#{${application.admin.passwords}}")
	private List<String> adminPasswords;

	private UserRepository userRepository;

	private RoleRepository roleRepository;

	private PasswordEncoder passwordEncoder;

//	private ApplicationProperties applicationProperties;

	public AppStartup(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
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
			User user = new User();
			user.setRoles(roles);
			user.setUsername(username);
			user.setPassword(adminPasswords.get(index++));
			user.setLanguage("FR");
			user.setCreatedBy("system");
			userRepository.save(user);
		}
	}

	private void createRoles() {
		for (RoleEnum role : RoleEnum.values()) {
			if (roleRepository.findById(role.name()).isEmpty()) {
				roleRepository.save(new Role(role));
			}
		}
		long numRoles = roleRepository.count();
		assert (numRoles == (long) RoleEnum.values().length);
	}
}
