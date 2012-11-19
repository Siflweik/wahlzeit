package org.wahlzeit.services.mailing;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.wahlzeit.services.EmailAddress;
import org.wahlzeit.services.SysLog;

public abstract class AbstractEmailServer implements EmailServer {

	@Override
	public void sendEmail(EmailAddress from, EmailAddress to, String subject, String body) throws MailingException {
		sendEmail(from, to, EmailAddress.NONE, subject, body);
	}
	
	@Override
	public void sendEmail(EmailAddress from, EmailAddress to, EmailAddress bcc, String subject, String body) throws MailingException {
		assertAddressHasValidSyntax(from, "sender");
		assertAddressHasValidSyntax(to, "receiver");

		assertIsValidString(subject, "subject");
		assertIsValidString(body, "body");

		try	{
			Message msg = doCreateEmail(from, to, bcc, subject, body);
			doSendEmail(msg);
		} catch (MailingException ex)	{
			throw ex;
		} catch (Exception ex)	{
			throw new MailingException(ex);
		}
	}

	@Override
	public boolean sendEmailSilently(EmailAddress from, EmailAddress to, String subject, String body) {
		return sendEmailSilently(from, to, EmailAddress.NONE, subject, body);
	}
	
	@Override
	public boolean sendEmailSilently(EmailAddress from, EmailAddress to, EmailAddress bcc, String subject, String body) {
		boolean success = true;
		
		try {
			sendEmail(from, to, bcc, subject, body);
		} catch (MailingException e) {
			success = false;
			SysLog.logThrowable(e);
		}
		
		return success;
	}
	
	protected void assertAddressHasValidSyntax(EmailAddress address, String label) throws MailingException {
		boolean error = (address == null || address.asString() == null);

		if (!error) {
			// Check for compliance with RFC 822
			try {
				InternetAddress internetAddress = new InternetAddress(address.asString());
				internetAddress.validate();
			} catch (Exception ex) {
				error = true;
			}
		}

		if (error) {
			throw new MailingException(label + " must have a valid email address");
		}
	}

	protected void assertIsValidString(String toBeChecked, String label) throws MailingException {
		if (toBeChecked == null || toBeChecked.trim().equals("")) {
			throw new MailingException(label + " must neither be null nor empty");
		}
	}

	protected abstract Message doCreateEmail(EmailAddress from, EmailAddress to, EmailAddress bcc, String subject, String body) throws Exception;
	protected abstract void doSendEmail(Message msg) throws Exception;
}
