package org.wahlzeit.services.persistence.serializers;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.persistence.Serializer;
import org.wahlzeit.services.persistence.serializers.basic.StringSerializer;
import org.wahlzeit.utils.StringUtil;

public class URLSerializer extends Serializer<URL, Object> {
	private final StringSerializer stringSerializer = new StringSerializer();
	
	@Override
	public void writeOn(ResultSet result, String key, URL value) throws SQLException {
		stringSerializer.writeOn(result, key, value.toString());
	}

	@Override
	public URL readFrom(ResultSet result, String key) throws SQLException {
		return StringUtil.asUrl(stringSerializer.readFrom(result, key));
	}

}
