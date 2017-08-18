package me.bincode.party.exceptions;


public class InvitationException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public InvitationException(String message) {
		super(message);
	}

	public InvitationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvitationException(Throwable cause) {
		super(cause);
	}

	public InvitationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
