package org.wahlzeit.services.mailing;

public class MailingException extends Exception {
	private static final long serialVersionUID = 1L;

	public MailingException(String reason)	{
		super(reason);
	}
	
	public MailingException(Exception other)	{
		super(getTypedExceptionMessage(other));
	}
	
	protected static String getTypedExceptionMessage(Exception ex)	{
		String cause = "null";
		
		if (ex != null)	{
			cause = "[" + ex.getClass().getName() + "] " + ex.getMessage();
		}
		
		return cause;
	}
}
