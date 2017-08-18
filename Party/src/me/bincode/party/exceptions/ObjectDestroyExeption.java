package me.bincode.party.exceptions;

public class ObjectDestroyExeption extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public ObjectDestroyExeption(String message) {
		super(message);
	}

	public ObjectDestroyExeption(String message, Throwable cause) {
		super(message, cause);
	}

	public ObjectDestroyExeption(Throwable cause) {
		super(cause);
	}

	public ObjectDestroyExeption(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
