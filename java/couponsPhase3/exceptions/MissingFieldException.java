package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class MissingFieldException extends Exception {

	public MissingFieldException() {
		
		super("One or more form fields missing");
	}
}