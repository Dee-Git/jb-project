package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class CompanyExistsException extends Exception {

	public CompanyExistsException() {
		
		super("Company already exists in the database");
	}
}