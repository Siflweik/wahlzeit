package org.wahlzeit.handlers;

import org.wahlzeit.main.Wahlzeit;
import org.wahlzeit.model.PhotoManager;

public class ConfiguredWahlzeit extends Wahlzeit {

	public ConfiguredWahlzeit() {
		super(null, null, new MockEmailServer(), new WebPartHandlerManager(), new PhotoManager(null));
	}
}
