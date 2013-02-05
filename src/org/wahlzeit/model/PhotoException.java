package org.wahlzeit.model;

public class PhotoException extends Exception {
	private static final long serialVersionUID = 1L;

	public PhotoException(Exception cause)	{
		super(cause);
	}
}
