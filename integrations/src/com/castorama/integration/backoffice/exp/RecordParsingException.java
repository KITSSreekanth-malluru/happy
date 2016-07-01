/**
 * 
 */
package com.castorama.integration.backoffice.exp;

/**
 * @author Andrew_Logvinov
 *
 */
class RecordParsingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;

	/**
	 * 
	 */
	public RecordParsingException() {
	}

	/**
	 * @param message
	 */
	public RecordParsingException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RecordParsingException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RecordParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}
