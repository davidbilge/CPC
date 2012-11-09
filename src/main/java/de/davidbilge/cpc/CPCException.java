package de.davidbilge.cpc;

public class CPCException extends RuntimeException {
	private static final long serialVersionUID = 3083398425746102843L;

	public CPCException(String message, Throwable cause) {
		super(message, cause);
	}

	public CPCException(Throwable cause) {
		super(cause);
	}

}
