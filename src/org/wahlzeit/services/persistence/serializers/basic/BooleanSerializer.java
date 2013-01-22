package org.wahlzeit.services.persistence.serializers.basic;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.persistence.Serializer;

public class BooleanSerializer extends Serializer<Boolean, Object> {

	@Override
	public void writeOn(ResultSet result, String key, Boolean value) throws SQLException {
		result.updateBoolean(key, value);
	}

	@Override
	public Boolean readFrom(ResultSet result, String key) throws SQLException {
		return result.getBoolean(key);
	}
}
