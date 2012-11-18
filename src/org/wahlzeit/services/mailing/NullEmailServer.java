package org.wahlzeit.services.mailing;

import org.wahlzeit.services.EmailAddress;
import org.wahlzeit.services.SysLog;

public class NullEmailServer implements EmailServer {

	@Override
	public void sendEmail(EmailAddress from, EmailAddress to, String subject, String body) throws MailingException {
		SysLog.logInfo("pretending to send email...");
	}
	
	@Override
	public void sendEmail(EmailAddress from, EmailAddress to, EmailAddress bcc, String subject, String body) throws MailingException {
		SysLog.logInfo("pretending to send email...");
	}
}
