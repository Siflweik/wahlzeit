package org.wahlzeit.services.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Serializer {
	public void writeOn(ResultSet result, String key, Object value) throws SQLException;
	public Object readFrom(ResultSet result, String key) throws SQLException;
}

