package org.wahlzeit.services.persistence.serializers;

import org.wahlzeit.services.Language;
import org.wahlzeit.services.persistence.serializers.basic.EnumSerializer;

public class LanguageSerializer extends EnumSerializer<Language> {

	@Override
	protected Language getFromInt(int intValue) {
		return Language.getFromInt(intValue);
	}
}
