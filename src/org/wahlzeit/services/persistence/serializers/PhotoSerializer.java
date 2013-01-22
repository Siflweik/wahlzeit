package org.wahlzeit.services.persistence.serializers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoId;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.services.persistence.Serializer;
import org.wahlzeit.services.persistence.serializers.basic.IntegerSerializer;

public class PhotoSerializer extends Serializer<Photo, Object> {
	private final IntegerSerializer intSerializer = new IntegerSerializer();
	
	@Override
	public void writeOn(ResultSet result, String key, Photo value) throws SQLException {
		intSerializer.writeOn(result, key, (value == null) ? 0 : value.getId().asInt());
	}

	@Override
	public Photo readFrom(ResultSet result, String key) throws SQLException {
		return PhotoManager.getPhoto(PhotoId.getFromInt(intSerializer.readFrom(result, key)));
	}
}
