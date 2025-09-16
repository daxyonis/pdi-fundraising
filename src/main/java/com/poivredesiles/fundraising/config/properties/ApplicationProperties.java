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

	private String baseUrl;

	private Mail mail;

	private boolean encrypted;

	private Mode mode;

	private Action action;

	private Pay pay;

	public static class Pay {
		private String url;
		private String merchantId;
		private String token;

		public String url() {
			return url;
		}

		public Pay setUrl(String url) {
			this.url = url;
			return this;
		}

		public String merchantId() {
			return merchantId;
		}

		public Pay setMerchantId(String merchantId) {
			this.merchantId = merchantId;
			return this;
		}

		public String token() {
			return token;
		}

		public Pay setToken(String token) {
			this.token = token;
			return this;
		}
	}

	public static class Action {

		private boolean encrypt;

		// Getters and setters
		public boolean isEncrypt() {
			return encrypt;
		}
		public void setEncrypt(boolean encrypt) {
			this.encrypt = encrypt;
		}

	}

	public static class Mode {
		private boolean maintenance;

		// Getters and setters
		public boolean isMaintenance() {
			return maintenance;
		}
		public void setMaintenance(boolean maintenance) {
			this.maintenance = maintenance;
		}
	}

	public static class Mail {

		private String admin;

		private String from;

		private String to;

		private String cc;

		private String pdi;

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

		public String getPdi() { return pdi; }

		public void setPdi(String pdi) { this.pdi = pdi; }
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}		

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Mode getMode() {
		return mode;
	}
	public Action getAction() {
		return this.action;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

	public Pay getPay() {
		return pay;
	}
	public void setPay(Pay pay) {
		this.pay = pay;
	}

}
