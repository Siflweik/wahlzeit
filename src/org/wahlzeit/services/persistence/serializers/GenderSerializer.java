package org.wahlzeit.services.persistence.serializers;

import org.wahlzeit.model.Gender;
import org.wahlzeit.services.persistence.serializers.basic.EnumSerializer;

public class GenderSerializer extends EnumSerializer<Gender> {

	@Override
	protected Gender getFromInt(int intValue) {
		return Gender.getFromInt(intValue);
	}
}
