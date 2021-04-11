package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class DoubleDipException extends Exception {

	public DoubleDipException() {
		
		super("This coupon purchase already exists in the database");
	}
}