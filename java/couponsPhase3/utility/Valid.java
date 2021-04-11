package couponsPhase3.utility;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Valid {

	private static final int Length = 64;

	/**
	 * Validates any name field (user name, first name, last name, company name).
	 * 
	 * @param string in need of validating.
	 * @return true if string answers bean criteria.
	 */
	public boolean companyOrUserName(String s) {

		s = s.strip();

		if (s.contains("."))
			return false;

		if (s.length() > Length)
			return false;

		if (!isBasicValid(s))
			return false;

		return true;
	}

	/**
	 * Validates email field.
	 * 
	 * @param string in need of validating.
	 * @return true if string answers bean criteria.
	 */
	public boolean eMail(String s) {

		s = s.strip();

		if (s.length() > Length)
			return false;

		if (!isBasicValid(s))
			return false;

		return true;
	}

	/**
	 * Validates password field.
	 * 
	 * @param string in need of validating.
	 * @return true if string answers bean criteria.
	 */
	public boolean password(String s) {

		s = s.strip();

		if (s.contains("."))
			return false;

		if (s.contains(" "))
			return false;

		if (s.length() > Length)
			return false;

		if (!isBasicValid(s))
			return false;

		return true;
	}

	/**
	 * Validates Coupon description field.
	 * 
	 * @param string in need of validating.
	 * @return true if string answers bean criteria.
	 */
	public boolean couponDesc(String s) {

		s = s.strip();

		if (s.length() > 2 * Length)
			return false;

		if (!isBasicValid(s))
			return false;

		return true;
	}

	/**
	 * Validates Coupon title field.
	 * 
	 * @param string in need of validating.
	 * @return true if string answers bean criteria.
	 */
	public boolean couponTitle(String s) {

		s = s.strip();

		if (s.length() > Length)
			return false;

		if (!isBasicValid(s))
			return false;

		return true;
	}

	//
	// Private
	//

	/**
	 * Checks for illegal chars, blank strings, null strings.
	 * 
	 * @param s
	 * @return true if string answers criteria.
	 */
	private boolean isBasicValid(String s) {

		if (s == null) {
			System.out.println("string null @ basic validation");
			return false;
		}

		if (s.isBlank()) {
			System.out.println("string blank @ basic validation");
			return false;
		}

		// TODO simplify/clean this up
		if (s.contains("{") || s.contains("}") || s.contains("?") || s.contains("\"") || s.contains(":")
				|| s.contains(";") || s.contains(",") || s.contains(">") || s.contains("<") || s.contains("|")
				|| s.contains("]") || s.contains("[") || s.contains("/") || s.contains("\\") || s.contains("*")
				|| s.contains("!"))
			return false; // TODO

		return true;
	}
}
