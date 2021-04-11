package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class BadCompanyException extends Exception {

	public BadCompanyException() {
		
		super("Invalid Company.\nOne or more of the entered fields do not meet their criteria.");
	}
}