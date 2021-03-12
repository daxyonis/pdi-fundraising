package com.poivredesiles.fundraising.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Application specific properties
 * 
 * @author Eva Maciejko
 *
 */
@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
public class ApplicationProperties {

	private Mail mail;	

	public static class Mail {

		private String admin;

		private String from;

		private String to;

		private String cc;

		public String getAdmin() {
			return admin;
		}

		public void setAdmin(String admin) {
			this.admin = admin;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public String getCc() {
			return cc;
		}

		public void setCc(String cc) {
			this.cc = cc;
		}

	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}		
	
}
