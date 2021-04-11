package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class BadCouponException extends Exception {

	public BadCouponException() {

		super("Invalid Coupon.\nOne or more of the entered fields do not meet their criteria.");
	}
}