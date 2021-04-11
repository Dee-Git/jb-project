package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class BadCompanyIdException extends Exception {

	public BadCompanyIdException() {
		
		super("Bad company ID");
	}
}