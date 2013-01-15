package org.wahlzeit.services.persistence.serializers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.persistence.Serializer;

public class IntegerSerializer implements Serializer {

	@Override
	public void writeOn(ResultSet result, String key, Object value) throws SQLException {
		result.updateInt(key, (Integer)value);
	}

	@Override
	public Object readFrom(ResultSet result, String key) throws SQLException {
		return result.getInt(key);
	}
}