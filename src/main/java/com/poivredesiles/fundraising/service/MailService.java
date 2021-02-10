package com.poivredesiles.fundraising.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import com.poivredesiles.fundraising.resource.ErrorMessage;

@Service
public class MailService {
	private final Logger log = LoggerFactory.getLogger(MailService.class);	

	private final JavaMailSender javaMailSender;

	private final MessageSource messageSource;

	private final SpringTemplateEngine templateEngine;
	
	private final ApplicationProperties applicationProperties;
	
	private final Environment env;

	public MailService(JavaMailSender javaMailSender, MessageSource messageSource, SpringTemplateEngine templateEngine, ApplicationProperties applicationProperties, Environment env) {

		this.javaMailSender = javaMailSender;
		this.messageSource = messageSource;
		this.templateEngine = templateEngine;
		this.applicationProperties = applicationProperties;
		this.env = env;
	}

	@Async
	public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}'", isMultipart,
				isHtml, to, subject);

		// Prepare message using a Spring helper
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
			message.setTo(to);
            message.setFrom(applicationProperties.getMail().getFrom());
            if(!applicationProperties.getMail().getCc().equals("none")) {
            	message.setCc(applicationProperties.getMail().getCc());
            }
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			log.debug("Sent email to User '{}'", to);
		} catch (MailException | MessagingException e) {
			log.warn("Email could not be sent to user '{}'", to, e);
		}
	}

//	@Async
//	public void sendEmailFromTemplate(Contact contact, String templateName, String titleKey) {
//		if (contact.getDealerEmail() == null) {
//			log.debug("Email doesn't exist for dealer '{}'", contact.getDealerId());
//			return;
//		}
//		Locale locale = Locale.FRENCH;
//		Context context = new Context(locale);
//		context.setVariable(CONTACT, contact);
//		String content = templateEngine.process(templateName, context);
//		String subject = messageSource.getMessage(titleKey, new Object[] {contact.getVehicleLabel()}, locale);
//		String to = getEmailTo(contact);
//		sendEmail(to, subject, content, false, true);
//	}
//
//	@Async
//	public void sendContactEmail(Contact contact) {
//		log.debug("Sending contact email to '{}'", getEmailTo(contact));
//		sendEmailFromTemplate(contact, "mail/contactEmail", "email.contact.title");
//	}
//	
//	private String getEmailTo(Contact contact) {
//		String to = applicationProperties.getMail().getTo();
//		
//		//******************************************
//		// Send to dealer only if in production
//		if(to.equals("dealer") && Arrays.asList(env.getActiveProfiles()).contains("prod")) {
//			to = contact.getDealerEmail();
//		}
//		//******************************************
//		return to;
//	}

	@Async
	public void sendErrorEmail(ErrorMessage errorMessage) {
		Locale locale = Locale.FRENCH;
		Context context = new Context(locale);
		context.setVariable("error", errorMessage);
		String content = templateEngine.process("mail/errorEmail", context);		
		String to = applicationProperties.getMail().getAdmin();
		sendEmail(to, errorMessage.getSubject(), content, false, true);		
	}

}
