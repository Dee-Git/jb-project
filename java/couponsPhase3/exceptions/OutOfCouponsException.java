package couponsPhase3.exceptions;

@SuppressWarnings("serial")
public class OutOfCouponsException extends Exception {

	public OutOfCouponsException() {

		super("The number of available (to purchase) coupons is zero");
	}
}
