package org.wahlzeit.services.mailing;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.wahlzeit.services.EmailAddress;

public class SmtpEmailServer extends AbstractEmailServer {
	private Session session;
	
	public SmtpEmailServer()	{
		this("localhost", "25", null, null);
	}
	
	public SmtpEmailServer(String host, String port)	{
		this(host, port, null, null);
	}
	
	public SmtpEmailServer(String host, String port, String user, String password)	{
		initialize(host, port, user, password);
	}
	
	/**
	 * 
	 * @methodtype initialization
	 */
	protected void initialize(String host, String port, String user, String password)	{
		Properties props = new Properties();
		
		props.setProperty("mail.smtp.host", host);
		props.setProperty("mail.smtp.port", port);
		
		Authenticator auth = null;
		
		if (user != null && password != null)	{
			auth = new SmtpAuthenticator(user, password);
			
			props.setProperty("mail.smtp.auth", "true");
		}
		
		session = Session.getDefaultInstance(props, auth);		
	}
	
	/**
	 * 
	 * @methodtype factory
	 * @methodproperties composed
	 */
	@Override
	protected Message doCreateEmail(EmailAddress from, EmailAddress to, EmailAddress bcc, String subject, String body) throws Exception {
		Message msg = new MimeMessage(session);
		
		msg.setFrom(new InternetAddress(from.asString()));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to.asString()));

		if (bcc != EmailAddress.NONE) {
			msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc.asString()));
		}

		msg.setSubject(subject);
		msg.setContent(createMultipart(body));
		
		return msg;
	}
	
	/**
	 * 
	 * @methodtype factory
	 * @methodproperties primitive, hook
	 */
	protected Multipart createMultipart(String body) throws MessagingException {
		Multipart mp = new MimeMultipart();
		BodyPart textPart = new MimeBodyPart();
		
		textPart.setText(body); // sets type to "text/plain"
		mp.addBodyPart(textPart);
		
		return mp;
	}
	
	@Override
	protected void doSendEmail(Message msg) throws Exception	{
		Transport.send(msg);	
	}
	
	protected class SmtpAuthenticator extends Authenticator	{
		private PasswordAuthentication auth;
		
		public SmtpAuthenticator(String user, String password)	{
			auth = new PasswordAuthentication(user, password);
		}
				
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return auth;
		}		
	}
}
