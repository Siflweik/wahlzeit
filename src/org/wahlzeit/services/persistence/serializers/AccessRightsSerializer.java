package org.wahlzeit.services.persistence.serializers;

import org.wahlzeit.model.AccessRights;
import org.wahlzeit.services.persistence.serializers.basic.EnumSerializer;

public class AccessRightsSerializer extends EnumSerializer<AccessRights>	{

	@Override
	protected AccessRights getFromInt(int intValue) {
		return AccessRights.getFromInt(intValue);
	}
}
