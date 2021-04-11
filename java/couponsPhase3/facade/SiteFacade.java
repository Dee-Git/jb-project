package couponsPhase3.facade;

import java.security.NoSuchAlgorithmException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import couponsPhase3.exceptions.BadCustomerException;
import couponsPhase3.exceptions.BadLoginException;
import couponsPhase3.exceptions.CustomerExistsException;
import couponsPhase3.tables.Customer;
import couponsPhase3.utility.Encrypt;

/**
 * Facade for site (client type) actions such as registering new end users.
 * 
 * @author D
 *
 */
@Service
@Scope("singleton")
public class SiteFacade extends ClientFacade {

	public SiteFacade() {
	}

	/**
	 * site will login into db with hardwired pw and email to track registering
	 * actions.
	 * pw; siteAction
	 */
	@Override
	public int login(String email, String password) throws BadLoginException, NoSuchAlgorithmException {

		if (isValid.eMail(email) && isValid.password(password))
			if (email.equals("action@site.net") && Encrypt.SHA3_512(password).equals(
					"74738d744551b13ce84e35c90fe234590ee86dbdcdb5b60c2efea7f8a816273b5a147caf444478d2078e7ff2fbe8848b3a395d4396df6f7179874d348a14c43e"))
				return Integer.MAX_VALUE;

		throw new BadLoginException();
	}

	// TODO complete mock method. adds user to db.
	/**
	 * Add a new customer.
	 * 
	 * @param New customer
	 * @return the saved entity; will never be null.
	 * @throws BadCustomerException           for invalid fields
	 * @throws NoSuchAlgorithmException       Encrypt class exception
	 * @throws CustomerExistsException        SPRING exception
	 * @throws EmptyResultDataAccessException SPRING exception
	 */
	public Customer registerCustomer(Customer customer)
			throws BadCustomerException, NoSuchAlgorithmException, CustomerExistsException {

		if (customer == null)
			throw new BadCustomerException();

		if (customer.getClass() != Customer.class || customer.getId() != 0)
			throw new BadCustomerException(); // new object id must be 0

		if (isValid.eMail(customer.getEmail()) && isValid.password(customer.getPassword())
				&& isValid.companyOrUserName(customer.getFirstName())
				&& isValid.companyOrUserName(customer.getLastName())) {

			customer.setFirstName(customer.getFirstName().strip());
			customer.setLastName(customer.getLastName().strip());
			customer.setEmail(customer.getEmail().strip());
			customer.setPassword(Encrypt.SHA3_512(customer.getPassword().strip()));
		} else
			throw new BadCustomerException();

		if (customerRepository.existsByEmail(customer.getEmail()))
			throw new CustomerExistsException();

		return customerRepository.save(customer);
	}
}