package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class BadLoginException extends Exception {

	public BadLoginException() {
		
		super("Bad Username or Password");
	}
}