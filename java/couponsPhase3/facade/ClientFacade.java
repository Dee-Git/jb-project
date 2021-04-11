package couponsPhase3.facade;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import couponsPhase3.exceptions.BadLoginException;
import couponsPhase3.repos.CompanyRepository;
import couponsPhase3.repos.CouponRepository;
import couponsPhase3.repos.CustomerRepository;
import couponsPhase3.utility.Valid;

@Service
public abstract class ClientFacade {

	@Autowired
	protected CompanyRepository companyRepository;
	@Autowired
	protected CouponRepository couponRepository;
	@Autowired
	protected CustomerRepository customerRepository;

	// Internal use utility
	@Autowired
	protected Valid isValid;

	/**
	 * Check credentials against values in database
	 * 
	 * @param email
	 * @param password
	 * @return associated ID if email AND pw match db. Will return Integer.MAX_VALUE
	 *         for admin login.
	 * @throws BadLoginException
	 */
	public abstract int login(String email, String password) throws BadLoginException, NoSuchAlgorithmException;

}