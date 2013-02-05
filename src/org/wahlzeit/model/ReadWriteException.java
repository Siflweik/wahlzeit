package org.wahlzeit.model;

import java.sql.SQLException;

public class ReadWriteException extends Exception {
	private static final long serialVersionUID = 1L;

	public ReadWriteException(SQLException cause)	{
		super(cause);
	}
}
