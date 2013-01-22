package org.wahlzeit.services.persistence.serializers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.model.PhotoId;
import org.wahlzeit.services.persistence.Serializer;
import org.wahlzeit.services.persistence.serializers.basic.IntegerSerializer;

public class PhotoIdSerializer extends Serializer<PhotoId, Object> {
	private final IntegerSerializer intSerializer = new IntegerSerializer();
	
	@Override
	public void writeOn(ResultSet result, String key, PhotoId value) throws SQLException {
		intSerializer.writeOn(result, key, value.asInt());
	}

	@Override
	public PhotoId readFrom(ResultSet result, String key) throws SQLException {
		return PhotoId.getId(intSerializer.readFrom(result, key));
	}
}
