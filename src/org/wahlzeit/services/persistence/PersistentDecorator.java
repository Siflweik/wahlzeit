package org.wahlzeit.services.persistence;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.wahlzeit.services.Persistent;

// Since there is no multiple inheritance in java, this decorator class has to be used in order to store classes which already have a superclass
public class PersistentDecorator extends PersistentObject {
	private Persistent persistent;

	public PersistentDecorator(Persistent persistent) {
		this.persistent = persistent;
	}

	@Override
	protected Persistent getPersistent() {
		return persistent;
	}

	@Override
	public String getIdAsString() {
		return "";
	}

	@Override
	public void writeId(PreparedStatement stmt, int pos) throws SQLException {
		// do nothing
	}
}