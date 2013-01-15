package org.wahlzeit.services.persistence;

public @interface PersistentConvertableField {
	Class<? extends Converter> converterClass();
}
