package org.wahlzeit.services.persistence.serializers.basic;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.persistence.Serializer;
import org.wahlzeit.utils.EnumValue;

// If we made the assumption that all subclasses of EnumValue had the same static conversion method, we would not need this class
public abstract class EnumSerializer<E extends EnumValue> extends Serializer<E, Object> {
	protected final IntegerSerializer intSerializer = new IntegerSerializer();
	
	@Override
	public void writeOn(ResultSet result, String key, E value) throws SQLException {
		intSerializer.writeOn(result, key, value.asInt());
	}

	@Override
	public E readFrom(ResultSet result, String key) throws SQLException {
		int intValue = intSerializer.readFrom(result, key);
		
		return getFromInt(intValue);
	}
	
	protected abstract E getFromInt(int intValue);
}
