package org.wahlzeit.services.persistence.serializers;

import org.wahlzeit.model.FlagReason;
import org.wahlzeit.services.persistence.serializers.basic.EnumSerializer;

public class FlagReasonSerializer extends EnumSerializer<FlagReason> {

	@Override
	protected FlagReason getFromInt(int intValue) {
		return FlagReason.getFromInt(intValue);
	}
}
