package org.wahlzeit.services.persistence.serializers;

import org.wahlzeit.model.UserStatus;
import org.wahlzeit.services.persistence.serializers.basic.EnumSerializer;

public class UserStatusSerializer extends EnumSerializer<UserStatus> {

	@Override
	protected UserStatus getFromInt(int intValue) {
		return UserStatus.getFromInt(intValue);
	}
}
