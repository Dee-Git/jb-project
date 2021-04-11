package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class InvalidCouponDateException extends Exception {

	public InvalidCouponDateException() {
		
		super("Invalid coupon start or end date");
	}
}