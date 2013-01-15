package org.wahlzeit.model.clients.roles;

import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.clients.ClientCore;
import org.wahlzeit.model.clients.ClientRole;

public class AdministratorRole extends ClientRole {

	public AdministratorRole(ClientCore core) {
		super(AccessRights.ADMINISTRATOR, core);
	}
}
