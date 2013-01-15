package org.wahlzeit.model;

import java.util.HashMap;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.wahlzeit.model.clients.ClientRole;
import org.wahlzeit.model.clients.roles.AdministratorRole;
import org.wahlzeit.model.clients.roles.GuestRole;
import org.wahlzeit.model.clients.roles.ModeratorRole;
import org.wahlzeit.model.clients.roles.RegisteredUserRole;
import org.wahlzeit.model.clients.roles.RoleIndex;

public class RoleIndexTest extends TestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(RoleIndexTest.class);
	}
	
	private HashMap<AccessRights, Class<? extends ClientRole>> classes = new HashMap<AccessRights, Class<? extends ClientRole>>();
	
	@Override
	protected void setUp() throws Exception {
		classes.put(AccessRights.GUEST, GuestRole.class);
		classes.put(AccessRights.USER, RegisteredUserRole.class);
		classes.put(AccessRights.MODERATOR, ModeratorRole.class);
		classes.put(AccessRights.ADMINISTRATOR, AdministratorRole.class);
	}
	
	public void testGetRoleByAccessRights()	{
		for (Entry<AccessRights, Class<? extends ClientRole>> pair : classes.entrySet()) {
			Class<? extends ClientRole> actual = RoleIndex.getRoleByAccessRights(pair.getKey());
			
			assertTrue(actual.equals(pair.getValue()));
		}
	}
}
