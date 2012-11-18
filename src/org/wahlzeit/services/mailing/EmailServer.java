package org.wahlzeit.services.mailing;

import org.wahlzeit.services.EmailAddress;

public interface EmailServer {
	
	public void sendEmail(EmailAddress from, EmailAddress to, String subject, String body) throws MailingException;
	public void sendEmail(EmailAddress from, EmailAddress to, EmailAddress bcc, String subject, String body) throws MailingException;
}
