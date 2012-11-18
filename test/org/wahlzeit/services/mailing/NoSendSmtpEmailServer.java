package org.wahlzeit.services.mailing;

import java.util.Stack;

import javax.mail.Message;

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
		Message msg = null;
		
		if (!messages.isEmpty())	{
			msg = messages.pop();
		}
		
		return msg;
	}
}
