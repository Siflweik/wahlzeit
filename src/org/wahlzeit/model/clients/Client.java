package org.wahlzeit.model.clients;

import org.wahlzeit.services.EmailAddress;

public interface Client {
	
	public <R extends ClientRole> R getRoleByName(Class<R> clazz);
	
	public boolean hasRole(Class<? extends ClientRole> role);
	public void addRole(ClientRole role);
	public void removeRole(Class<? extends ClientRole> clazz);
	
	public EmailAddress getEmailAddress();
	public void setEmailAddress(EmailAddress emailAddress);
}
