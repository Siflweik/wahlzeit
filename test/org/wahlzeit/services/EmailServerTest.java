package org.wahlzeit.services;

import org.wahlzeit.services.mailing.MailingException;
import org.wahlzeit.services.mailing.NoSendSmtpEmailServer;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class EmailServerTest extends TestCase {

	private NoSendSmtpEmailServer emailServer;
	private EmailAddress invalidAddress1;
	private EmailAddress invalidAddress2;
	private EmailAddress validAddress;
	
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(EmailServerTest.class));
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		emailServer = new NoSendSmtpEmailServer();
		
		invalidAddress1 = new EmailAddress("");
		invalidAddress2 = new EmailAddress("testtest.de");
		validAddress = new EmailAddress("test@test.de");
	}
	
	public void testInvalidAddresses()	{
		try	{
			assertFalse(emailServer.sendEmailSilently(null, null, "lol", "hi"));
			assertFalse(emailServer.sendEmailSilently(invalidAddress1, invalidAddress1, "hi", "body"));
			assertFalse(emailServer.sendEmailSilently(invalidAddress2, invalidAddress2, "hi", "body"));
		} catch (Exception ex)	{
			fail("Silent mode does not allow exceptions");
		}
	}
	
	public void testInvalidData()	{
		try	{
			assertFalse(emailServer.sendEmailSilently(validAddress, validAddress, "", "body"));
			assertFalse(emailServer.sendEmailSilently(validAddress, validAddress, "hi", "       "));
		} catch (Exception ex)	{
			fail("Silent mode does not allow exceptions");
		}
	}

	public void testSendValidMessage()	{
		try	{
			assertTrue(emailServer.sendEmailSilently(validAddress, validAddress, "hi", "test"));
			assertEquals(1, emailServer.getMessageCount());
		} catch (Exception ex)	{
			fail("Must not fail");
		}
	}
}
