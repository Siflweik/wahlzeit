package org.wahlzeit.model.clients;

import java.util.HashMap;

import org.wahlzeit.services.EmailAddress;
import org.wahlzeit.services.SysLog;

public class ClientCore implements Client {
	private HashMap<Class<? extends ClientRole>, ClientRole> roles;
	private EmailAddress emailAddress;
	
	public ClientCore()	{
		this(EmailAddress.EMPTY);
	}
	
	public ClientCore(EmailAddress address)	{
		emailAddress = address;
		roles = new HashMap<Class<? extends ClientRole>, ClientRole>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <R extends ClientRole> R getRoleByName(Class<R> clazz)	{
		ClientRole role = roles.get(clazz);
		
		try	{
			return (R)role;
		} catch (Exception ex)	{
		}
	
		SysLog.logError("Role '" + clazz + "' could not be found!");
		return null;
	}
	
	@Override
	public boolean hasRole(Class<? extends ClientRole> clazz) {
		return roles.containsKey(clazz);
	}

	@Override
	public void addRole(ClientRole role) {
		if (role != null)	{
			roles.put(role.getClass(), role);
		}
	}

	@Override
	public void removeRole(ClientRole role) {
		if (role != null)	{
			roles.remove(role.getAccessRights());
		}
	}
	
	@Override
	public EmailAddress getEmailAddress() {
		return emailAddress;
	}

	@Override
	public void setEmailAddress(EmailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}
}
