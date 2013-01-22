package org.wahlzeit.services.persistence.serializers;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.model.User;
import org.wahlzeit.services.persistence.Serializer;
import org.wahlzeit.services.persistence.serializers.basic.StringSerializer;
import org.wahlzeit.utils.StringUtil;

public class HomepageSerializer extends Serializer<URL, User> {
	private final StringSerializer stringSerializer = new StringSerializer();
	
	@Override
	public void writeOn(ResultSet result, String key, URL value) throws SQLException {
		stringSerializer.writeOn(result, key, value.toString());
	}

	@Override
	public URL readFrom(ResultSet result, String key) throws SQLException {
		return StringUtil.asUrlOrDefault(stringSerializer.readFrom(result, key), getOwner().getDefaultHomePage());
	}
}
