package org.wahlzeit.services.persistence.serializers.basic;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.persistence.Serializer;

public class StringSerializer extends Serializer<String, Object> {

	@Override
	public void writeOn(ResultSet result, String key, String value) throws SQLException {
		result.updateString(key, (String)value);
	}

	@Override
	public String readFrom(ResultSet result, String key) throws SQLException {
		return result.getString(key);
	}

}