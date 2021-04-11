package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class OutOfDateCouponException extends Exception {

	public OutOfDateCouponException() {
		
		super("Coupon is out of date");
	}
}