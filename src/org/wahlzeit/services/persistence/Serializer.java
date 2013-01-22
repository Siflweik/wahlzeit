package org.wahlzeit.services.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Serializer<Data, Owner> {
	private Owner owner = null;

	public void setOwner(Owner owner)	{
		this.owner = owner;
	}
	
	protected Owner getOwner()	{
		return owner;
	}
	
	public abstract void writeOn(ResultSet result, String key, Data value) throws SQLException;
	public abstract Data readFrom(ResultSet result, String key) throws SQLException;
}

