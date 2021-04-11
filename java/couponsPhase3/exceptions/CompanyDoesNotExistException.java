package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class CompanyDoesNotExistException extends Exception {

	public CompanyDoesNotExistException() {
		
		super("Company ID does not exist in the database");
	}
}