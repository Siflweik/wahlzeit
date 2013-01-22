package org.wahlzeit.services.persistence.serializers;

import org.wahlzeit.model.PhotoStatus;
import org.wahlzeit.services.persistence.serializers.basic.EnumSerializer;

public class PhotoStatusSerializer extends EnumSerializer<PhotoStatus> {

	@Override
	protected PhotoStatus getFromInt(int intValue) {
		return PhotoStatus.getFromInt(intValue);
	}
}
