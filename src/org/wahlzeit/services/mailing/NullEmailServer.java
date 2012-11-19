package org.wahlzeit.services.mailing;

import org.wahlzeit.services.EmailAddress;
import org.wahlzeit.services.SysLog;

public class NullEmailServer implements EmailServer {

	@Override
	public void sendEmail(EmailAddress from, EmailAddress to, String subject, String body) throws MailingException {
		SysLog.logInfo("pretending to send email (no bcc)");
	}
	
	@Override
	public void sendEmail(EmailAddress from, EmailAddress to, EmailAddress bcc, String subject, String body) throws MailingException {
		SysLog.logInfo("pretending to send email (with bcc)");
	}

	@Override
	public boolean sendEmailSilently(EmailAddress from, EmailAddress to, String subject, String body) {
		SysLog.logInfo("pretending to send email (silently, no bcc)");
		
		return true;
	}

	@Override
	public boolean sendEmailSilently(EmailAddress from, EmailAddress to, EmailAddress bcc, String subject, String body) {
		SysLog.logInfo("pretending to send email (silently with bcc)");
		
		return true;
	}
}
