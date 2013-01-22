package org.wahlzeit.services.persistence.serializers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.EmailAddress;
import org.wahlzeit.services.persistence.Serializer;
import org.wahlzeit.services.persistence.serializers.basic.StringSerializer;

public class EmailAddressSerializer extends Serializer<EmailAddress, Object> {
	private final StringSerializer stringSerializer = new StringSerializer();

	@Override
	public void writeOn(ResultSet result, String key, EmailAddress value) throws SQLException {
		stringSerializer.writeOn(result, key, (value == null) ? "" : value.asString());
	}

	@Override
	public EmailAddress readFrom(ResultSet result, String key) throws SQLException {
		return EmailAddress.getFromString(stringSerializer.readFrom(result, key));
	}
}
