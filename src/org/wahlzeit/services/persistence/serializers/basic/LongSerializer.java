package org.wahlzeit.services.persistence.serializers.basic;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.persistence.Serializer;

public class LongSerializer extends Serializer<Long, Object> {

	@Override
	public void writeOn(ResultSet result, String key, Long value) throws SQLException {
		result.updateLong(key, value);
	}

	@Override
	public Long readFrom(ResultSet result, String key) throws SQLException {
		return result.getLong(key);
	}
}