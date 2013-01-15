package org.wahlzeit.model.clients.roles;

import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.clients.ClientCore;
import org.wahlzeit.model.clients.ClientRole;

public class ModeratorRole extends ClientRole {

	public ModeratorRole(ClientCore core) {
		super(AccessRights.MODERATOR, core);
	}
}
