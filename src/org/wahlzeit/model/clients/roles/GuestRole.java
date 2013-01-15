package org.wahlzeit.model.clients.roles;

import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.clients.ClientCore;
import org.wahlzeit.model.clients.ClientRole;

public class GuestRole extends ClientRole {

	public GuestRole(ClientCore core) {
		super(AccessRights.GUEST, core);
	}
}
