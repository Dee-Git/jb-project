package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class BadCustomerException extends Exception {

	public BadCustomerException() {
		
		super("Invalid Customer.\nOne or more of the entered fields do not meet their criteria.");
	}
}