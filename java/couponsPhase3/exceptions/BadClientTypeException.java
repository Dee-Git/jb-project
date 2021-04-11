package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class BadClientTypeException extends Exception {

	public BadClientTypeException() {
		
		super("Invalid Category");
	}
}