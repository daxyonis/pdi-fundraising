package com.poivredesiles.fundraising.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	// The public endpoints
	private static final String[] PUBLIC = new String[]{"/error", "/login", "/logout"}; 
	
	// Provide a bean for the password encoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}	 

	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        	.antMatchers(PUBLIC).permitAll()      
        	.antMatchers("/").hasAnyRole("BUYER", "SELLER", "GROUP_LEADER", "CAMPAIGN_LEADER", "ADMIN") 
        	.antMatchers("/commande/**", "/api/checkout/**").hasRole("BUYER")
        	.antMatchers("/ventes").hasRole("SELLER")
        	.antMatchers("/synthese/**").hasAnyRole("CAMPAIGN_LEADER","GROUP_LEADER")
            .antMatchers("/admin", "/api/file/**").hasRole("ADMIN")            
            .anyRequest().authenticated()
            .and()
            .formLogin()
            	.loginPage("/login")
            .and()
            .logout()           
            	.logoutUrl("/logout");            	
    }
	
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/static/**", "/js/**", "/css/**","/image/**", "/favicon.ico","/webjars/**");
	}
}
