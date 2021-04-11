package couponsPhase3.facade;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import couponsPhase3.exceptions.BadCompanyException;
import couponsPhase3.exceptions.BadCompanyIdException;
import couponsPhase3.exceptions.BadCustomerException;
import couponsPhase3.exceptions.BadLoginException;
import couponsPhase3.exceptions.BadNumberInputException;
import couponsPhase3.exceptions.CompanyDoesNotExistException;
import couponsPhase3.exceptions.CompanyExistsException;
import couponsPhase3.exceptions.CustomerDoesNotExistException;
import couponsPhase3.exceptions.CustomerExistsException;
import couponsPhase3.tables.Company;
import couponsPhase3.tables.Coupon;
import couponsPhase3.tables.Customer;
import couponsPhase3.utility.Encrypt;

@Service
@Scope("singleton")
public class AdminFacade extends ClientFacade {

	/**
	 * Here be dragons
	 * 
	 * @param companyRepository
	 * @param couponRepository
	 * @param customerRepository
	 */
	public AdminFacade() {

	}

	@Override
	public int login(String email, String password) throws BadLoginException, NoSuchAlgorithmException {

		if (isValid.eMail(email) && isValid.password(password))
			if (email.equals("admin@admin.com") && Encrypt.SHA3_512(password).equals(
					"5a38afb1a18d408e6cd367f9db91e2ab9bce834cdad3da24183cc174956c20ce35dd39c2bd36aae907111ae3d6ada353f7697a5f1a8fc567aae9e4ca41a9d19d"))
				return Integer.MAX_VALUE;

		throw new BadLoginException();
	}

	//
	// Companies
	//

	/**
	 * Add a new company to db.
	 * 
	 * @param New company
	 * @return the saved entity; will never be null.
	 * @throws BadCompanyException            if any of the fields are invalid
	 * @throws CompanyExistsException         if email or name already in db
	 * @throws NoSuchAlgorithmException       Encrypt class exception
	 * @throws EmptyResultDataAccessException SPRING exception
	 */
	public Company addCompany(Company company)
			throws BadCompanyException, CompanyExistsException, BadNumberInputException, NoSuchAlgorithmException {

		if (company == null)
			throw new BadCompanyException();

		if (company.getClass() != Company.class || company.getId() != 0)
			throw new BadCompanyException(); // new object id must be 0

		if (isValid.eMail(company.getEmail()) && isValid.password(company.getPassword())
				&& isValid.companyOrUserName(company.getName())) {

			company.setEmail(company.getEmail().strip());
			company.setPassword(Encrypt.SHA3_512(company.getPassword().strip()));
			company.setName(company.getName().strip());
		} else
			throw new BadCompanyException();

		if (companyRepository.existsByEmail(company.getEmail()) || companyRepository.existsByName(company.getName()))
			throw new CompanyExistsException();

		return companyRepository.save(company);
	}

	/**
	 * Admin use. Update an existing (by id) company.
	 * 
	 * @param valid company
	 * @return the saved entity.
	 * @throws BadNumberInputException                   if company.id is invalid
	 * @throws BadCompanyException                       if any of the fields are
	 *                                                   invalid
	 * @throws CompanyExistsException                    if new email or name
	 *                                                   already in db
	 * @throws CompanyDoesNotExistException              if company.id isn't in db
	 * @throws NoSuchAlgorithmException                  Encrypt class exception
	 * @throws IllegalArgumentException                  SPRING exception
	 * @throws EmptyResultDataAccessException            SPRING exception
	 * @throws javax.persistence.EntityNotFoundException
	 */
	public Company updateCompany(Company company) throws BadCompanyException, CompanyExistsException,
			BadNumberInputException, CompanyDoesNotExistException, NoSuchAlgorithmException {

		if (company == null)
			throw new BadCompanyException();

		if (company.getClass() != Company.class)
			throw new BadCompanyException();

		if (company.getId() <= 0)
			throw new BadNumberInputException();

		if (!(companyRepository.existsById(company.getId())))
			throw new CompanyDoesNotExistException();

		if (isValid.eMail(company.getEmail()) && isValid.password(company.getPassword())
				&& isValid.companyOrUserName(company.getName())) {

			company.setEmail(company.getEmail().strip());
			company.setPassword(Encrypt.SHA3_512(company.getPassword().strip()));
			company.setName(company.getName().strip());

			Company old = companyRepository.findById(company.getId()).orElseThrow();

			if (!old.getEmail().equals(company.getEmail())) {
				// If new email; check new email against database
				if (companyRepository.existsByEmail(company.getEmail())) {
					throw new CompanyExistsException();
				}

			}
			if (!old.getName().equals(company.getName())) {
				// If new name; check new name against database
				if (companyRepository.existsByName(company.getName())) {
					throw new CompanyExistsException();
				}
			}
		} else
			throw new BadCompanyException();

		return companyRepository.save(company);

	}

	/**
	 * Delete ALL db entries associated with company id.
	 * 
	 * @param company id
	 * @throws BadNumberInputException  for invalid id's
	 * @throws NoSuchElementException   SPRING exception
	 * @throws IllegalArgumentException SPRING exception
	 */
	public void deleteCompany(int id) throws BadNumberInputException {

		if (id <= 0)
			throw new BadNumberInputException();

		boolean flag;

		List<Coupon> cPurchases = new ArrayList<Coupon>();
		Company company = getOneCompany(id);

		for (Customer customer : getAllCustomers()) {

			flag = false;

			// Collect every purchases of company coupons (there might be more than one)
			for (Coupon coupon : customer.getCoupons()) {
				if (coupon.getCompanyId() == id) {
					cPurchases.add(coupon);
					flag = true; // Purchase found
				}
			}

			if (flag) { // Remove purchases and update customer
				customer.getCoupons().removeAll(cPurchases);
				customerRepository.save(customer);
			}

			cPurchases.clear();
		}

		couponRepository.deleteInBatch(company.getCoupons());

//		for (Coupon coupon : company.getCoupons()) {
//			couponRepository.deleteById(coupon.getId());
//			System.out.println("Coupon #" + coupon.getId() + " deleted.");
//		} // Delete company coupons

		// Delete company
		companyRepository.delete(getOneCompany(id));
		System.out.println("Company #" + id + " deleted @ AdminFacade.");
	}

	/**
	 * 
	 * @return list of all companies, or empty list.
	 */
	public List<Company> getAllCompanies() {

		return companyRepository.findAll();
	}

	/**
	 * Get company, including company coupons.
	 * 
	 * @param companyId
	 * @return company with same id in db.
	 * @throws BadNumberInputException  for invalid id's
	 * @throws NoSuchElementException   SPRING exception
	 * @throws IllegalArgumentException SPRING exception
	 */
	public Company getOneCompany(int companyId) throws BadNumberInputException {

		if (companyId <= 0)
			throw new BadNumberInputException();

		return companyRepository.findById(companyId).orElseThrow();
	}

	//
	// Customers
	//

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
	public Customer addCustomer(Customer customer)
			throws BadCustomerException, CustomerExistsException, BadNumberInputException, NoSuchAlgorithmException {

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

	/**
	 * Admin use. Update an existing (by id) customer.
	 * 
	 * @param valid customer
	 * @return the saved entity.
	 * @throws CustomerDoesNotExistException             if customer.id isn't in db
	 * @throws BadCustomerException                      if any of the fields are
	 *                                                   invalid
	 * @throws BadNumberInputException                   for invalid id's
	 * @throws CustomerExistsException                   if new customer.email is
	 *                                                   already in db
	 * @throws NoSuchAlgorithmException                  Encrypt class exception
	 * @throws IllegalArgumentException                  SPRING exception
	 * @throws EmptyResultDataAccessException            SPRING exception
	 * @throws javax.persistence.EntityNotFoundException
	 */
	public Customer updateCustomer(Customer customer) throws CustomerDoesNotExistException, BadCustomerException,
			BadNumberInputException, CustomerExistsException, NoSuchAlgorithmException {

		if (customer == null)
			throw new BadCustomerException();

		if (!(customer.getClass() == Customer.class))
			throw new BadCustomerException();

		if (customer.getId() <= 0)
			throw new BadNumberInputException();

		if (!(customerRepository.existsById(customer.getId())))
			throw new CustomerDoesNotExistException();

		if (isValid.eMail(customer.getEmail()) && isValid.password(customer.getPassword())
				&& isValid.companyOrUserName(customer.getFirstName())
				&& isValid.companyOrUserName(customer.getLastName())) {

			customer.setFirstName(customer.getFirstName().strip());
			customer.setLastName(customer.getLastName().strip());
			customer.setEmail(customer.getEmail().strip());
			customer.setPassword(Encrypt.SHA3_512(customer.getPassword().strip()));

			if (!customerRepository.findById(customer.getId()).orElseThrow().getEmail().equals(customer.getEmail())) {
				// If new email; check new email against database
				if (customerRepository.existsByEmail(customer.getEmail())) {
					throw new CustomerExistsException();
				}
			}

		} else
			throw new BadCustomerException();

		return customerRepository.save(customer);

	}

	/**
	 * Delete all entries associated with customer id in db.
	 * 
	 * @param valid id
	 * @throws BadNumberInputException  for invalid id's
	 * @throws IllegalArgumentException SPRING exception
	 * @throws NoSuchElementException   SPRING exception
	 */
	public void deleteCustomer(int id) throws BadNumberInputException {

		if (id <= 0)
			throw new BadNumberInputException();

		Customer customer = customerRepository.findById(id).orElseThrow();

		List<Coupon> coupons = new ArrayList<Coupon>();
		coupons.addAll(customer.getCoupons());

		customer.getCoupons().removeAll(coupons);

		// Remove any existing purchases
		customerRepository.save(customer);

		customerRepository.delete(customer);

		System.out.println("Customer #" + id + " deleted @ AdminFacade.");
	}

	/**
	 * For when you need to search for company coupons in the purchases table.
	 * 
	 * @return a list of all customers in db, or empty list.
	 */
	public List<Customer> getAllCustomers() {

		return customerRepository.findAll();
	}

	/**
	 * 
	 * @param customerId
	 * @return Customer entity, including coupons.
	 * @throws BadNumberInputException  for invalid id's
	 * @throws IllegalArgumentException SPRING exception
	 * @throws NoSuchElementException   SPRING exception
	 */
	public Customer getOneCustomer(int customerId) throws BadNumberInputException {

		if (customerId <= 0)
			throw new BadNumberInputException();

		return customerRepository.findById(customerId).orElseThrow();
	}

	//
	// Coupons
	//

	/**
	 * 
	 * @return List of all Coupons found, or empty list.
	 */
	public List<Coupon> getAllCoupons() {

		return couponRepository.findAll();
	}

	/**
	 * Delete ALL coupon references from the database according to coupon ID. This
	 * includes purchased coupons.
	 * 
	 * @param valid coupon ID
	 * @throws BadCompanyIdException    from nested methods
	 * @throws BadNumberInputException  for invalid id's
	 * @throws IllegalArgumentException SPRING exception
	 * @throws NoSuchElementException   SPRING exception
	 */
	public void deleteCoupon(int couponId) throws BadCompanyIdException, BadNumberInputException {

		// Validation
		if (couponId <= 0)
			throw new BadNumberInputException();
		couponRepository.findById(couponId).orElseThrow();

//		// Update purchases for ALL customers
//		for (Customer customer : customerRepository.findAll()) {
//			if (customer.getCoupons().contains(c)) {
//				customer.getCoupons().remove(c);
//				customerRepository.save(customer);
//			}
//		} // remove purchase and update customer
		// Old

		Coupon tempC = new Coupon();

		// Update purchases for ALL customers
		for (Customer customer : getAllCustomers()) {
			for (Coupon coupon : customer.getCoupons()) {
				if (coupon.getId() == couponId) {
					tempC = coupon;
					break; // There can only be one purchase of couponId
				}
			}
			if (tempC.getId() != 0) {
				customer.getCoupons().remove(tempC);
				tempC = new Coupon();
				customerRepository.save(customer);
			} // remove purchase and update customer
		}

		// Remove coupon
		couponRepository.deleteById(couponId);
		System.out.println("Coupon #" + couponId + " deleted @ AdminFacade.");

	} // Delete coupon
}