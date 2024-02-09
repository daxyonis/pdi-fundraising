package com.poivredesiles.fundraising.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
	
	// The public endpoints
	private static final String[] PUBLIC = new String[]{"/error", "/login", "/logout", "/actuator/**"}; 
	
	// Provide a bean for the password encoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
				.csrf(csrf -> csrf.ignoringAntMatchers("/api/global/response"))
				.formLogin(login -> login.loginPage("/login"))
				.logout(logout -> logout.logoutUrl("/logout"))
				.authorizeHttpRequests(authz -> authz
					.antMatchers(PUBLIC).permitAll()
					.antMatchers("/api/global/response").permitAll()
					.antMatchers("/").hasAnyRole("BUYER", "SELLER", "GROUP_LEADER", "CAMPAIGN_LEADER", "ADMIN")
					.antMatchers("/commande/**", "/api/global/checkout").hasRole("BUYER")
					.antMatchers("/ventes").hasRole("SELLER")
					.antMatchers("/synthese/**").hasAnyRole("CAMPAIGN_LEADER","GROUP_LEADER")
					.antMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN")
					.antMatchers(HttpMethod.POST, "/admin", "/api/file/**").hasRole("ADMIN")
						.antMatchers("/api/admin/**").hasRole("ADMIN")
					.antMatchers(HttpMethod.GET,"/api/file/**").hasAnyRole("BUYER", "SELLER", "GROUP_LEADER", "CAMPAIGN_LEADER", "ADMIN")
					.anyRequest().authenticated()
				);

		return http.build();
    }

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers("/static/**", "/js/**", "/css/**","/image/**", "/favicon.ico","/webjars/**");
	}

}
