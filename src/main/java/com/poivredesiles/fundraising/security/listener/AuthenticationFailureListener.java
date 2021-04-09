package com.poivredesiles.fundraising.security.listener;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.poivredesiles.fundraising.model.security.LoginFailure;
import com.poivredesiles.fundraising.model.security.LoginFailure.LoginFailureBuilder;
import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.repository.security.LoginFailureRepository;
import com.poivredesiles.fundraising.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listens to authentication failure events
 * @author evita
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener {
	
	private final LoginFailureRepository loginFailureRepository;
	private final UserRepository userRepository;
	
	@Value("${application.account.loginAttempts}")
	private Integer numLoginAttempts;
	
	@Value("${application.account.loginTimespan}")
	private Integer loginTimespan;

	@EventListener
	public void listen(AuthenticationFailureBadCredentialsEvent event) {
		
		log.info("User logged in failed");		
		
		if(event.getSource() instanceof UsernamePasswordAuthenticationToken) {
			LoginFailureBuilder builder = LoginFailure.builder();
			
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();
			//log.debug("User {} tried to login with credentials {}", token.getPrincipal(), token.getCredentials());
			if(token.getPrincipal() instanceof String) {
				String username = (String)token.getPrincipal();
				builder.username(username);
			
				if(userRepository.countByUsername(username) > 0) {
					User user = userRepository.getUserByUsername(username);
					builder.user(user);					
				}
			}
			
			if(token.getDetails() instanceof WebAuthenticationDetails) {
				WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();				
				builder.sourceIp(details.getRemoteAddress());
			}
			
			LoginFailure loginFailure = loginFailureRepository.save(builder.build());
			
			log.debug("Login failure, id= {}", loginFailure.getId());
			
			if(loginFailure.getUser() != null) {
				lockUserAccount(loginFailure.getUser());
			}
		}
		
		
	}

	/**
	 * Lock user account if > N failures in M hours (for now N=3, M=24h)
	 * TODO: externalize N and M
	 * @param user
	 */
	private void lockUserAccount(User user) {
		int numFailures = loginFailureRepository.countAllByUserAndCreatedDateIsAfter(user, Timestamp.valueOf(LocalDateTime.now().minusDays(loginTimespan)));
		
		if(numFailures > numLoginAttempts) {
			log.debug("Locking User account id={}", user.getId());
			user.setLocked(true);
			userRepository.save(user);
		}
	}
}
