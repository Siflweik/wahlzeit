package org.wahlzeit.services.persistence.serializers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.model.Tags;
import org.wahlzeit.services.persistence.Serializer;
import org.wahlzeit.services.persistence.serializers.basic.StringSerializer;

public class TagsSerializer extends Serializer<Tags, Object> {
	private final StringSerializer stringSerializer = new StringSerializer();
	
	@Override
	public void writeOn(ResultSet result, String key, Tags value) throws SQLException {
		stringSerializer.writeOn(result, key, value.asString());
	}

	@Override
	public Tags readFrom(ResultSet result, String key) throws SQLException {
		return new Tags(stringSerializer.readFrom(result, key));
	}
}
