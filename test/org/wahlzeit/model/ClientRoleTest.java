package org.wahlzeit.model;

import java.util.LinkedList;

import org.wahlzeit.model.clients.ClientCore;
import org.wahlzeit.model.clients.ClientRole;
import org.wahlzeit.model.clients.roles.AdministratorRole;
import org.wahlzeit.model.clients.roles.GuestRole;
import org.wahlzeit.model.clients.roles.ModeratorRole;
import org.wahlzeit.model.clients.roles.RegisteredUserRole;

import junit.framework.TestCase;

public class ClientRoleTest extends TestCase {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ClientRoleTest.class);
	}
	
	private LinkedList<Class<? extends ClientRole>> roles = new LinkedList<Class<? extends ClientRole>>();	
	
	@Override
	protected void setUp() throws Exception {
		roles.add(GuestRole.class);
		roles.add(RegisteredUserRole.class);
		roles.add(ModeratorRole.class);
		roles.add(AdministratorRole.class);
	}
	
	public void testEmptyClient()	{
		ClientCore core = new ClientCore();
		
		for (Class<? extends ClientRole> role : roles) {
			assertFalse(core.hasRole(role));
		}
	}
	
	public void testAddRole()	{
		ClientCore core = new ClientCore();
				
		assertFalse(core.hasRole(ModeratorRole.class));
		
		core.addRole(new ModeratorRole(core));
		
		assertTrue(core.hasRole(ModeratorRole.class));
	}
	
	public void testRemoveRole()	{
		ClientCore core = new ClientCore();
		
		core.addRole(new ModeratorRole(core));
		
		assertTrue(core.hasRole(ModeratorRole.class));
		
		core.removeRole(ModeratorRole.class);
		
		assertFalse(core.hasRole(ModeratorRole.class));
		
		
	}
}
