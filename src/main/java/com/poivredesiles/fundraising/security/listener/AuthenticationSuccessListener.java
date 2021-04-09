package com.poivredesiles.fundraising.security.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.poivredesiles.fundraising.model.user.MyUserDetails;

import lombok.extern.slf4j.Slf4j;

/**
 * Listens to authentication success events
 * @author evita
 *
 */
@Slf4j
@Component
public class AuthenticationSuccessListener {

	@EventListener
	public void listen(AuthenticationSuccessEvent event) {
		
		log.info("User logged in ok");
		
		if(event.getSource() instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();
			
			if(token.getPrincipal() instanceof MyUserDetails) {
				MyUserDetails user = (MyUserDetails) token.getPrincipal();
				
				log.debug("User name logged in: {}", user.getUsername());
			}
			
			if(token.getDetails() instanceof WebAuthenticationDetails) {
				WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
				
				log.debug("Source IP: " + details.getRemoteAddress());
			}
		}
	}
}
