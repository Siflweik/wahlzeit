package org.wahlzeit.services.mailing;

import java.util.Stack;

import javax.mail.Message;

import junit.framework.Assert;

public class NoSendSmtpEmailServer extends SmtpEmailServer {

	protected Stack<Message> messages;
	
	public NoSendSmtpEmailServer()	{
		messages = new Stack<Message>();
	}
	
	@Override
	protected synchronized void doSendEmail(Message msg) throws Exception {
		messages.push(msg);
	}
	
	public synchronized Message getLastMessage()	{
		Assert.assertFalse("There is no email :(", messages.isEmpty());

		return messages.pop();
	}
	
	public synchronized int getMessageCount()	{
		return messages.size();
	}
}
