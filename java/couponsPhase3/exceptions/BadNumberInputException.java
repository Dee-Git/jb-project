package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class BadNumberInputException extends Exception {

	public BadNumberInputException() {
		
		super("Invalid number value");
	}
}