package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class CustomerDoesNotExistException extends Exception {

	public CustomerDoesNotExistException() {
		
		super("Customer ID does not exist in the database");
	}
}