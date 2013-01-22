package org.wahlzeit.services.persistence.serializers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.model.CaseId;
import org.wahlzeit.services.persistence.Serializer;
import org.wahlzeit.services.persistence.serializers.basic.IntegerSerializer;

public class CaseIdSerializer extends Serializer<CaseId, Object> {
	private final IntegerSerializer intSerializer = new IntegerSerializer();
	
	@Override
	public void writeOn(ResultSet result, String key, CaseId value) throws SQLException {
		intSerializer.writeOn(result, key, value.asInt());
	}

	@Override
	public CaseId readFrom(ResultSet result, String key) throws SQLException {
		return new CaseId(intSerializer.readFrom(result, key));
	}
}
