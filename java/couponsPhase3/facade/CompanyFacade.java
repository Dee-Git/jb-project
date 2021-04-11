package couponsPhase3.facade;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import couponsPhase3.exceptions.BadCategoryTypeException;
import couponsPhase3.exceptions.BadCompanyException;
import couponsPhase3.exceptions.BadCompanyIdException;
import couponsPhase3.exceptions.BadCouponException;
import couponsPhase3.exceptions.BadLoginException;
import couponsPhase3.exceptions.BadNumberInputException;
import couponsPhase3.exceptions.CompanyExistsException;
import couponsPhase3.exceptions.CouponNotFoundException;
import couponsPhase3.exceptions.InvalidCouponDateException;
import couponsPhase3.tables.Category;
import couponsPhase3.tables.Company;
import couponsPhase3.tables.Coupon;
import couponsPhase3.tables.Customer;
import couponsPhase3.utility.Encrypt;

@Service
@Scope("prototype")
public class CompanyFacade extends ClientFacade {

	private int companyId;

	/**
	 * Here be dragons
	 * 
	 * @param companyRepository
	 * @param couponRepository
	 * @param customerRepository
	 */
	public CompanyFacade() {

	}

	/**
	 * FOR LOGIN MANAGER USE
	 * 
	 * @param companyId
	 */
	public CompanyFacade(int companyId) {

		super();
		this.companyId = companyId;
	}

	@Override
	public int login(String email, String password) throws BadLoginException, NoSuchAlgorithmException {

		if (!isValid.eMail(email) && !isValid.password(password))
			throw new BadLoginException();

		Company company = companyRepository.findByEmailAndPassword(email, Encrypt.SHA3_512(password));

		if (company != null)
			return company.getId();

		throw new BadLoginException();

	}

	/**
	 * Company use. Update this.companyId company details.
	 * 
	 * @param company
	 * @return the saved entity.
	 * @throws BadCompanyException      if any of the fields are invalid
	 * @throws BadLoginException        is credentials don't match
	 * @throws BadNumberInputException  for invalid id's
	 * @throws CompanyExistsException   if new company email or name already exist
	 * @throws NoSuchAlgorithmException Encrypt class exception
	 * @throws NoSuchElementException   SPRING exception
	 */
	public Company updateCompany(Company company) throws BadCompanyException, BadLoginException,
			BadNumberInputException, CompanyExistsException, NoSuchAlgorithmException {

		if (company == null)
			throw new BadCompanyException();

		if (company.getClass() != Company.class)
			throw new BadCompanyException();

		if (company.getId() <= 0)
			throw new BadNumberInputException();

		if (company.getId() != this.companyId)
			throw new BadLoginException();

		if (isValid.eMail(company.getEmail()) && isValid.password(company.getPassword())
				&& isValid.companyOrUserName(company.getName())) {

			company.setEmail(company.getEmail().strip());
			// Clashes with testing
			company.setPassword(Encrypt.SHA3_512(company.getPassword().strip()));
//			company.setPassword(company.getPassword().strip());
			company.setName(company.getName().strip());

			Company old = companyRepository.findById(this.companyId).orElseThrow();

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
	 * Add a coupon to the associated company id.
	 * 
	 * @param New coupon
	 * @return saved entity
	 * @throws BadCategoryTypeException       for invalid enum
	 * @throws BadCompanyIdException          if this.companyId doesn't match
	 *                                        coupon.companyId
	 * @throws BadCouponException             if any of the coupon fields are
	 *                                        invalid
	 * @throws InvalidCouponDateException     if the dates don't answer bean
	 *                                        criteria
	 * @throws IllegalArgumentException       SPRING exception
	 * @throws EmptyResultDataAccessException SPRING exception
	 */
	public Coupon addCoupon(Coupon coupon) throws BadCategoryTypeException, BadCompanyIdException, BadCouponException,
			InvalidCouponDateException, BadNumberInputException {

		// Basic validity check
		if (coupon == null)
			throw new BadCouponException();
		if (coupon.getClass() != Coupon.class || coupon.getId() != 0)
			throw new BadCouponException(); // new object id must be 0
		// Verify coupon belongs to company
		if (coupon.getCompanyId() != companyId)
			throw new BadCompanyIdException();
		// Category check
		if (Category.class != coupon.getCategory().getClass())
			throw new BadCategoryTypeException();
		// Date check
		if (coupon.getStartDate().toLocalDate().isBefore(LocalDate.now())
				|| coupon.getEndDate().before(coupon.getStartDate()))
			throw new InvalidCouponDateException();
		// Fields check
		if (!isValid.couponDesc(coupon.getDescription()) || !isValid.couponTitle(coupon.getTitle())
				|| coupon.getAmount() <= 0 || coupon.getPrice() < 0)
			throw new BadCouponException();

		return couponRepository.save(coupon);
	}

	/**
	 * Update an existing coupon.
	 * 
	 * @param valid coupon
	 * @return the saved entity.
	 * @throws BadCompanyIdException          if this.companyId doesn't match
	 *                                        coupon.companyId
	 * @throws BadCategoryTypeException       for invalid enum
	 * @throws BadCouponException             if any of the coupon fields are
	 *                                        invalid
	 * @throws CouponNotFoundException        if coupon.id isn't in db
	 * @throws InvalidCouponDateException     if the dates don't answer bean
	 *                                        criteria
	 * @throws IllegalArgumentException       SPRING exception
	 * @throws EmptyResultDataAccessException SPRING exception
	 */
	public Coupon updateCoupon(Coupon coupon) throws BadCompanyIdException, BadCategoryTypeException,
			BadCouponException, CouponNotFoundException, InvalidCouponDateException, BadNumberInputException {

		// Basic validity check
		if (coupon == null)
			throw new BadCouponException();
		if (!(coupon.getClass() == Coupon.class) || coupon.getId() <= 0)
			throw new BadCouponException();
		// Existence check
		Coupon c = couponRepository.findById(coupon.getId()).orElse(null);
		if (c == null)
			throw new CouponNotFoundException();
		// Verify coupon belongs to company
		if (c.getCompanyId() != companyId)
			throw new BadCompanyIdException();
		// Category check
		if (Category.class != coupon.getCategory().getClass())
			throw new BadCategoryTypeException();
		// Date check
		if (coupon.getStartDate().compareTo(c.getStartDate()) != 0)
			if (coupon.getStartDate().toLocalDate().isBefore(LocalDate.now()))
				throw new InvalidCouponDateException();
		if (coupon.getEndDate().before(coupon.getStartDate()))
			throw new InvalidCouponDateException();
		// Fields check. New amount can be zero.
		if (!isValid.couponDesc(coupon.getDescription()) || !isValid.couponTitle(coupon.getTitle())
				|| coupon.getAmount() < 0 || coupon.getPrice() < 0)
			throw new BadCouponException();

		return couponRepository.save(coupon);
	}

	/**
	 * Delete ALL coupon references from the database according to coupon ID. This
	 * includes purchased coupons.
	 * 
	 * @param valid coupon ID
	 * @throws BadCompanyIdException    if the coupon associated with couponId
	 *                                  belongs to a company other than
	 *                                  this.companyId
	 * @throws BadNumberInputException  for invalid id's
	 * @throws NoSuchElementException   SPRING exception
	 * @throws IllegalArgumentException SPRING exception
	 */
	public void deleteCoupon(int couponId) throws BadCompanyIdException, BadNumberInputException {

		// Validation
		if (couponId <= 0)
			throw new BadNumberInputException(); // if valid id
		Coupon c = couponRepository.findById(couponId).orElseThrow(); // if coupon exists
		if (c.getCompanyId() != this.companyId) // if coupon belongs to this.companyId
			throw new BadCompanyIdException();

//		// Update purchases for ALL customers
//		for (Customer customer : customerRepository.findAll()) {
//			if (customer.getCoupons().contains(c)) {
//				customer.getCoupons().remove(c);
//				customerRepository.save(customer);
//			}
//		} // remove purchase and update customer
		// Old

		c = new Coupon();

		// Update purchases for ALL customers
		for (Customer customer : customerRepository.findAll()) {
			for (Coupon coupon : customer.getCoupons()) {
				if (coupon.getId() == couponId) {
					c = coupon;
					break;
				} // There can only be one purchase of couponId
			}

			if (c.getId() != 0) { // if tempC exists
				customer.getCoupons().remove(c);
				c = new Coupon();
				customerRepository.save(customer);
			} // remove purchase and update customer
		}

		// Remove coupon
		couponRepository.deleteById(couponId);
		System.out.println("Coupon #" + couponId + " deleted @ companyFacade.");
	}

	/**
	 * Find coupons associated with this company id
	 * 
	 * @return List of all Coupons found, or empty list.
	 */
	public List<Coupon> getCompanyCoupons() {

		return couponRepository.findAllByCompanyId(companyId);
	}

	/**
	 * Find coupons (in the category) associated with this company id
	 * 
	 * @param enum Category.value
	 * @return New Set<Coupon>
	 * @throws BadCategoryTypeException for invalid enum
	 */
	public List<Coupon> getCompanyCoupons(Category category) throws BadCategoryTypeException {

		if (category == null)
			throw new BadCategoryTypeException();
		if (Category.class != category.getClass())
			throw new BadCategoryTypeException();

		return couponRepository.findAllByCompanyIdAndCategory(companyId, category);
	}

	/**
	 * Find coupons (under or equal to maxPrice) associated with this company id
	 * 
	 * @param max coupon price
	 * @return New Set<Coupon>
	 * @throws BadNumberInputException for negative maxPrice
	 */
	public List<Coupon> getCompanyCoupons(double maxPrice) throws BadNumberInputException {

		if (maxPrice >= 0)
			return couponRepository.findAllByCompanyIdAndPriceLessThanEqual(companyId, maxPrice);

		throw new BadNumberInputException();
	}

	/**
	 * 
	 * @return Company entity, including coupons.
	 * @throws IllegalArgumentException SPRING exception
	 * @throws NoSuchElementException   SPRING exception
	 */
	public Company getCompanyDetails() {

		return companyRepository.findById(companyId).orElseThrow();
	}

	//
	// Class
	//

	public int getCompanyId() {
		return companyId;
	}

	/**
	 * LoginManager use
	 * 
	 * @param companyId
	 */
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
}