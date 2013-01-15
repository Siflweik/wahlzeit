package org.wahlzeit.model.clients.roles;

import java.util.HashMap;

import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.clients.ClientRole;

public class RoleIndex {
	private static HashMap<AccessRights, Class<? extends ClientRole>> classes = new HashMap<AccessRights, Class<? extends ClientRole>>();
	
	static	{
		classes.put(AccessRights.GUEST, GuestRole.class);
		classes.put(AccessRights.USER, RegisteredUserRole.class);
		classes.put(AccessRights.MODERATOR, ModeratorRole.class);
		classes.put(AccessRights.ADMINISTRATOR, AdministratorRole.class);
	}
	
	public static Class<? extends ClientRole> getRoleByAccessRights(AccessRights rights)	{
		return classes.get(rights);
	}
}
