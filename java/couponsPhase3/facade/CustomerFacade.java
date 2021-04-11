package couponsPhase3.facade;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import couponsPhase3.exceptions.BadCategoryTypeException;
import couponsPhase3.exceptions.BadCouponException;
import couponsPhase3.exceptions.BadCustomerException;
import couponsPhase3.exceptions.BadLoginException;
import couponsPhase3.exceptions.BadNumberInputException;
import couponsPhase3.exceptions.CouponNotFoundException;
import couponsPhase3.exceptions.CustomerExistsException;
import couponsPhase3.exceptions.DoubleDipException;
import couponsPhase3.exceptions.OutOfCouponsException;
import couponsPhase3.tables.Category;
import couponsPhase3.tables.Coupon;
import couponsPhase3.tables.Customer;
import couponsPhase3.utility.Encrypt;

@Service
@Scope("prototype")
public class CustomerFacade extends ClientFacade {

	private int customerId;

	/**
	 * Here be dragons
	 * 
	 * @param companyRepository
	 * @param couponRepository
	 * @param customerRepository
	 */
	public CustomerFacade() {

	}

	/**
	 * FOR LOGIN MANAGER USE
	 * 
	 * @param customerId
	 */
	public CustomerFacade(int customerId) {

		super();
		this.customerId = customerId;
	}

	@Override
	public int login(String email, String password) throws BadLoginException, NoSuchAlgorithmException {

		if (!isValid.eMail(email) && !isValid.password(password))
			throw new BadLoginException();

		Customer customer = customerRepository.findByEmailAndPassword(email, Encrypt.SHA3_512(password));

		if (customer != null)
			return customer.getId();

		throw new BadLoginException();

	}

	/**
	 * Customer use. Update this.customerId customer details.
	 * 
	 * @param customer
	 * @return the saved entity.
	 * @throws CustomerExistsException  if customer.email already exists
	 * @throws BadNumberInputException  if id is less than 1
	 * @throws BadCustomerException     if any of the fields are invalid
	 * @throws BadLoginException        if customer.id isn't this.id
	 * @throws NoSuchAlgorithmException Encrypt class exception
	 */
	public Customer updateCustomer(Customer customer) throws CustomerExistsException, BadNumberInputException,
			BadCustomerException, BadLoginException, NoSuchAlgorithmException {

		if (customer == null)
			throw new BadCustomerException();

		if (customer.getClass() != Customer.class)
			throw new BadCustomerException();

		if (customer.getId() <= 0)
			throw new BadNumberInputException();

		if (customer.getId() != this.customerId)
			throw new BadLoginException();

		if (isValid.eMail(customer.getEmail()) && isValid.password(customer.getPassword())
				&& isValid.companyOrUserName(customer.getFirstName())
				&& isValid.companyOrUserName(customer.getLastName())) {

			customer.setEmail(customer.getEmail().strip());
			// Clashes with testing
			customer.setPassword(Encrypt.SHA3_512(customer.getPassword().strip()));
//			customer.setPassword(customer.getPassword().strip());
			customer.setFirstName(customer.getFirstName().strip());
			customer.setLastName(customer.getLastName().strip());

			if (!getCustomerDetails().getEmail().equals(customer.getEmail())) {
				// If new email; check new email against database
				if (companyRepository.existsByEmail(customer.getEmail())) {
					throw new CustomerExistsException();
				}
			}

		} else
			throw new BadCustomerException();

		return customerRepository.save(customer);
	}

	//////////////
	////////////// Purchasing Coupons
	//////////////

	/**
	 * Add a purchase of coupon.id for associated customer id. Coupon must already
	 * exist in database.
	 * 
	 * @param a VALID coupon of Coupon class
	 * @throws BadCouponException             if any of the fields are invalid
	 * @throws OutOfCouponsException          if the amount in the db is 0
	 * @throws CouponNotFoundException        if coupon.id isn't in db
	 * @throws DoubleDipException             if coupon.id is already associated
	 *                                        with this.customerId
	 * @throws BadNumberInputException        for bad id's
	 * @throws IllegalArgumentException       SPRING exception
	 * @throws EmptyResultDataAccessException SPRING exception
	 */
	public void purchaseCoupon(Coupon coupon) throws BadCouponException, OutOfCouponsException, CouponNotFoundException,
			DoubleDipException, BadNumberInputException {

		if (coupon == null)
			throw new BadNumberInputException();

		if (coupon.getId() <= 0)
			throw new BadNumberInputException();

		if (coupon.getClass() != Coupon.class)
			throw new BadCouponException();

		Coupon c = couponRepository.findById(coupon.getId()).orElse(null);

		// Check coupon Id is in DB
		if (c == null)
			throw new CouponNotFoundException();

		// Coupon should match the one in the database
		if (c.hashCode() != coupon.hashCode())
			throw new BadCouponException();

		if (coupon.getAmount() <= 0)
			throw new OutOfCouponsException();

		// Oh boy, we can finally try a purchase
		Customer customer = getCustomerDetails();

		// Look for coupon in customer's purchases
		for (Coupon customerCoupon : customer.getCoupons()) {

			if (customerCoupon.getId() == coupon.getId()) {
				// The coupon has already been purchased by this.customerId
				throw new DoubleDipException();
			}
		}

		// Update; coupon.amount ; purchase ; coupon ; user
		coupon.setAmount(coupon.getAmount() - 1);
		customer.getCoupons().add(coupon);
		couponRepository.save(coupon);
		customerRepository.save(customer);

	}

	/**
	 * 
	 * @return List of all Coupons found, or empty list.
	 */
	public List<Coupon> getAllCoupons() {

		return couponRepository.findAll();
	}

	/**
	 * Find all coupons in Category in database.
	 * 
	 * @param enum Category.value
	 * @return New Set<Coupon>
	 * @throws BadCategoryTypeException for invalid enum
	 */
	public List<Coupon> getCoupons(Category category) throws BadCategoryTypeException {

		if (category == null)
			throw new BadCategoryTypeException();

		if (Category.class != category.getClass())
			throw new BadCategoryTypeException();

		return couponRepository.findAllByCategory(category);
	}

	/**
	 * Find all coupons under or equal to maxPrice in database.
	 * 
	 * @param max coupon price
	 * @return New Set<Coupon>
	 * @throws BadNumberInputException for negative maxPrice
	 */
	public List<Coupon> getCoupons(double maxPrice) throws BadNumberInputException {

		if (maxPrice >= 0)
			return couponRepository.findAllByPriceLessThanEqual(maxPrice);

		throw new BadNumberInputException();
	}

	//////////////
	////////////// Customer Coupons
	//////////////

	/**
	 * Get all coupons associated with customer ID. Will throw if for some reason
	 * customerId is invalid.
	 * 
	 * @return Set<Coupon> getCoupons()
	 * @throws NoSuchElementException   SPRING exception
	 * @throws IllegalArgumentException SPRING exception
	 */
	public Set<Coupon> getCustomerCoupons() {

		return customerRepository.findById(customerId).orElseThrow().getCoupons();
	}

	/**
	 * Get all coupons (in category) associated with customer ID. Will throw if for
	 * some reason customerId is invalid.
	 * 
	 * @param enum Category.value
	 * @return New Set<Coupon>. Will never be null.
	 * @throws BadCategoryTypeException for invalid enum
	 * @throws NoSuchElementException   from this.getCustomerCoupons()
	 */
	public Set<Coupon> getCustomerCoupons(Category category) throws BadCategoryTypeException {

		if (category == null)
			throw new BadCategoryTypeException();

		if (Category.class == category.getClass()) {

			Set<Coupon> c = new HashSet<Coupon>();

			for (Coupon coupon : getCustomerCoupons()) {

				if (coupon.getCategory().equals(category))
					c.add(coupon);
			} // Add all coupons in category

			return c;
		}

		throw new BadCategoryTypeException();

	}

	/**
	 * Get all coupons (under or equal to maxPrice) associated with customer ID.
	 * Will throw if for some reason customerId is invalid.
	 * 
	 * @param coupon max price
	 * @return New Set<Coupon>. Will never be null.
	 * @throws BadNumberInputException if maxPrice negative or zero
	 * @throws NoSuchElementException  from this.getCustomerCoupons()
	 */
	public Set<Coupon> getCustomerCoupons(double maxPrice) throws BadNumberInputException {

		if (maxPrice >= 0) {

			Set<Coupon> c = new HashSet<Coupon>();

			for (Coupon coupon : getCustomerCoupons()) {

				if (coupon.getPrice() <= maxPrice)
					c.add(coupon);
			} // Add all coupons under or equal to maxPrice

			return c;
		}

		throw new BadNumberInputException();
	}

	//////////////
	////////////// Customer
	//////////////

	/**
	 * For when you need Customer details but not their coupons.
	 * 
	 * @param Customer ID
	 * @return Customer fields without Coupon array
	 * @throws BadNumberInputException for bad id's
	 */
	public Customer getOneCustomer(int id) throws BadNumberInputException {

		if (id <= 0)
			throw new BadNumberInputException();

		return customerRepository.getOne(id);
	}

	/**
	 * 
	 * @return Customer entity, including coupons.
	 * @throws IllegalArgumentException SPRING exception
	 * @throws NoSuchElementException   SPRING exception
	 */
	public Customer getCustomerDetails() {

		return customerRepository.findById(customerId).orElseThrow();

	}

	//
	// Class
	//

	public int getCustomerId() {
		return customerId;
	}

	/**
	 * LoginManager use
	 * 
	 * @param customerId
	 */
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
}
