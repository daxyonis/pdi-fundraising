package com.poivredesiles.fundraising.config;

import com.poivredesiles.fundraising.filter.MaintenanceModeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
	
	// The public endpoints
	private static final String[] PUBLIC = new String[]{"/error", "/login", "/logout", "/actuator/**","/static/**", "/js/**", "/css/**", "/image/**", "/favicon.ico", "/webjars/**"};

	// Provide a bean for the password encoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Autowired
	private MaintenanceModeFilter maintenanceModeFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
				.addFilterBefore(maintenanceModeFilter, UsernamePasswordAuthenticationFilter.class)
				.csrf(csrf -> csrf.ignoringRequestMatchers("/api/pay/callback"))
				.formLogin(login -> login.loginPage("/login"))
				.logout(logout -> logout.logoutUrl("/logout"))
				.authorizeHttpRequests(authz -> authz
					.requestMatchers(PUBLIC).permitAll()
					.requestMatchers("/api/pay/callback").permitAll()
					.requestMatchers("/").hasAnyRole("BUYER", "SELLER", "GROUP_LEADER", "CAMPAIGN_LEADER", "ADMIN")
					.requestMatchers("/commande/**", "/api/pay/checkout").hasRole("BUYER")
					.requestMatchers("/ventes").hasRole("SELLER")
					.requestMatchers("/synthese/**").hasAnyRole("CAMPAIGN_LEADER","GROUP_LEADER")
					.requestMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST, "/admin", "/api/file/**").hasRole("ADMIN")
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET, "/api/file/**").hasAnyRole("BUYER", "SELLER", "GROUP_LEADER", "CAMPAIGN_LEADER", "ADMIN")
					.anyRequest().authenticated()
				);

		return http.build();
    }
}
