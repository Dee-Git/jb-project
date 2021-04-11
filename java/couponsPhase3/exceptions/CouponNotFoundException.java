package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class CouponNotFoundException extends Exception {

	public CouponNotFoundException() {
		
		super("Coupon Id does not appear in the database");
	}
}