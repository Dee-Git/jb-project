package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class CustomerExistsException extends Exception {

	public CustomerExistsException() {
		
		super("Customer already exists in the database");
	}
}