package org.wahlzeit.model.clients;

import java.security.InvalidParameterException;

import org.wahlzeit.model.AccessRights;
import org.wahlzeit.services.EmailAddress;

public abstract class ClientRole implements Client {
	//May be removed in the future; kept due to legacy code
	private AccessRights rights;
	
	private ClientCore core;
	
	protected ClientRole(AccessRights rights, ClientCore core)	{
		initialize(rights, core);
	}
	
	protected void initialize(AccessRights rights, ClientCore core)	{
		assertIsValidCore(core);
		
		this.rights = rights;
		this.core = core;
	}
	
	protected void assertIsValidCore(ClientCore core)	{
		if (core == null)	{
			throw new InvalidParameterException("core must not be null");
		}
	}

	@Override
	public <R extends ClientRole> R getRoleByName(Class<R> clazz)	{
		return core.getRoleByName(clazz);
	}

	@Override
	public boolean hasRole(Class<? extends ClientRole> role) {
		return core.hasRole(role);
	}
	
	@Override
	public void addRole(ClientRole role) {
		core.addRole(role);
	}
	
	@Override
	public void removeRole(ClientRole role) {
		core.removeRole(role);
	}
	
	public AccessRights getAccessRights()	{
		return rights;
	}
	
	protected void setAccessRights(AccessRights rights)	{
		this.rights = rights;
	}

	@Override
	public EmailAddress getEmailAddress() {
		return core.getEmailAddress();
	}

	@Override
	public void setEmailAddress(EmailAddress emailAddress) {
		core.setEmailAddress(emailAddress);
	}
}
