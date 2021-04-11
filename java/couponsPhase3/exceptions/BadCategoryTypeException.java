package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class BadCategoryTypeException extends Exception {

	public BadCategoryTypeException() {
		
		super("Invalid Category");
	}
}