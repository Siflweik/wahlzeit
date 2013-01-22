package org.wahlzeit.services.persistence.serializers.basic;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.persistence.Serializer;

public class IntegerSerializer extends Serializer<Integer, Object> {

	@Override
	public void writeOn(ResultSet result, String key, Integer value) throws SQLException {
		result.updateInt(key, value);
	}

	@Override
	public Integer readFrom(ResultSet result, String key) throws SQLException {
		return result.getInt(key);
	}
}