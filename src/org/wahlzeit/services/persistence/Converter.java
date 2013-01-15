package org.wahlzeit.services.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Converter implements Serializer	{
	private Serializer serializer = null;
	
	public void setSerializer(Serializer serializer)	{
		this.serializer = serializer;
	}

	@Override
	public void writeOn(ResultSet result, String key, Object value) throws SQLException {
		serializer.writeOn(result, key, convert(value));
	}

	@Override
	public Object readFrom(ResultSet result, String key) throws SQLException {
		return revert(serializer.readFrom(result, key));
	}
	
	protected abstract Object convert(Object dynamic);
	protected abstract Object revert(Object persistent);
}