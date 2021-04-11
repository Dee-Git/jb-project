package couponsPhase3.web;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import couponsPhase3.exceptions.BadCategoryTypeException;
import couponsPhase3.exceptions.BadCompanyIdException;
import couponsPhase3.exceptions.BadCouponException;
import couponsPhase3.exceptions.BadNumberInputException;
import couponsPhase3.exceptions.CouponNotFoundException;
import couponsPhase3.facade.CompanyFacade;
import couponsPhase3.login.Session;
import couponsPhase3.tables.Category;
import couponsPhase3.tables.Coupon;

import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("company")
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {

	@Autowired
	Map<String, Session> sMap;

	public CompanyController() {
	}

	//
	// Check credentials
	//

	/**
	 * Check for session timeout, and for authorized (Company) facade class. Will
	 * set session to null if true.
	 * 
	 * @param session: the current Session
	 * @param token:   UUID
	 */
	private void sessionCheck(String token, Session session) {

		if (session != null) {

			if ((System.currentTimeMillis() - session.getLastAccessed()) > 1800000
					|| session.getClientFacade().getClass() != CompanyFacade.class) {
				// This is either a session timeout or an unauthorized action attempt
				sMap.remove(token);
				session = null;
			} else
				session.setLastAccessed(System.currentTimeMillis()); // Update session time
		}
	}

	//////////////////
	////////////////// Coupon
	//////////////////

	/**
	 * Add a coupon to the associated company id.
	 * 
	 * @param a NEW valid coupon of class Coupon
	 * @return "Coupon added successfully with id " + c.getId() + "." where c is the
	 *         returned entity OR exception message
	 * @throws BadCategoryTypeException
	 * @throws BadCompanyIdException
	 * @throws BadCouponException
	 * @throws CouponIdExistsException
	 * @throws IllegalArgumentException
	 * @throws EmptyResultDataAccessException
	 */
	@PostMapping("/coupon/add/{token}")
	public ResponseEntity<Object> addCoupon(@PathVariable String token, @RequestBody Coupon coupon) {

		if (token != null) {

			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				if (coupon == null)
					return ResponseEntity.badRequest().body("Bad request\nError 400");

				try {

					CompanyFacade cFacade = (CompanyFacade) session.getClientFacade();
					Coupon c = cFacade.addCoupon(coupon);
					return ResponseEntity.ok("Coupon added successfully with id " + c.getId() + ".");

				} catch (BadCategoryTypeException e) {
					return ResponseEntity.badRequest().body("Invalid category.");
				} catch (BadCompanyIdException e) {
					return ResponseEntity.badRequest().body("Invalid ID.");
				} catch (BadCouponException e) {
					return ResponseEntity.badRequest().body("One or more entity fields are missing.");
				} catch (EmptyResultDataAccessException e) {
					return ResponseEntity.badRequest().body("Could not add coupon.");
				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/**
	 * Update an existing coupon.
	 * 
	 * @param valid coupon
	 * @return "Coupon updated successfully." OR exception message.
	 * @throws BadCompanyIdException
	 * @throws BadCategoryTypeException
	 * @throws BadCouponException
	 * @throws CouponNotFoundException
	 * @throws IllegalArgumentException
	 * @throws EmptyResultDataAccessException
	 */
	@PutMapping("/coupon/update/{token}")
	public ResponseEntity<Object> updateCoupon(@PathVariable String token, @RequestBody Coupon coupon) {

		if (token != null) {

			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				if (coupon == null)
					return ResponseEntity.badRequest().body("Bad request\nError 400");

				try {

					CompanyFacade cFacade = (CompanyFacade) session.getClientFacade(); // Guaranteed by .web.WebConfig
					cFacade.updateCoupon(coupon);
					return ResponseEntity.ok("Coupon updated successfully.");

				} catch (BadCompanyIdException e) {
					return ResponseEntity.badRequest().body("Coupon ID does not match Company ID");
				} catch (BadCategoryTypeException e) {
					return ResponseEntity.badRequest().body("Invalid category.");
				} catch (CouponNotFoundException e) {
					return ResponseEntity.badRequest().body("Coupon ID not found.");
				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
				// catch (EmptyResultDataAccessException e) {
				// Should not occur; handled by CouponNotFoundException }
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/**
	 * Delete ALL coupon references from the database according to coupon ID. This
	 * includes purchased coupons.
	 * 
	 * @param valid coupon ID
	 * @return "Coupon data removed successfully." OR exception message.
	 * @throws BadCompanyIdException
	 * @throws NoSuchElementException
	 * @throws BadNumberInputException
	 * @throws IllegalArgumentException
	 */
	@DeleteMapping("/coupon/delete/{token}/{id}")
	public ResponseEntity<Object> deleteCoupon(@PathVariable String token, @PathVariable int id) {

		if (token != null) {

			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					CompanyFacade cFacade = (CompanyFacade) session.getClientFacade(); // Guaranteed by .web.WebConfig
					cFacade.deleteCoupon(id);
					return ResponseEntity.ok("Coupon deleted.");

				} catch (BadCompanyIdException e) {
					ResponseEntity.badRequest().body("Coupon ID does not match Company ID.");
				} catch (NoSuchElementException e) {
					ResponseEntity.badRequest().body("Coupon ID not found.");
				} catch (BadNumberInputException e) {
					ResponseEntity.badRequest().body("Invalid coupon ID.");
				} catch (Exception e) {
					ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/**
	 * Find coupons associated with this company id
	 * 
	 * @return List of all Coupons found, or empty list.
	 */
	@GetMapping("/coupon/all/{token}")
	public ResponseEntity<Object> getAllCoupons(@PathVariable String token) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					CompanyFacade cFacade = (CompanyFacade) session.getClientFacade(); // Guaranteed by .web.WebConfig
					return ResponseEntity.ok(cFacade.getCompanyCoupons());

				} catch (Exception e) {
					ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/**
	 * Find coupons (in the category) associated with this company id.
	 * 
	 * @param valid category name.
	 * @return List of all Coupons found, or empty list
	 * @throws BadCategoryTypeException
	 */
	@GetMapping("/coupon/allcategory/{token}/{category}")
	public ResponseEntity<Object> getAllCoupons(@PathVariable String token, @PathVariable String category) {

		if (token != null) {

			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (category == null)
				return ResponseEntity.badRequest().body("Invalid category.");

			if (session != null) {

				try {

					CompanyFacade cFacade = (CompanyFacade) session.getClientFacade(); // Guaranteed by .web.WebConfig
					return ResponseEntity.ok(cFacade.getCompanyCoupons(Category.valueOf(category)));

				} catch (BadCategoryTypeException e) {
					return ResponseEntity.badRequest().body("Invalid category.");
				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/**
	 * Find coupons (under or equal to maxPrice) associated with this company id
	 * 
	 * @param max coupon price
	 * @return List of all Coupons found, or empty list.
	 * @throws BadNumberInputException
	 */
	@GetMapping("/coupon/allprice/{token}/{price}")
	public ResponseEntity<Object> getAllCoupons(@PathVariable String token, @PathVariable double price) {

		if (token != null) {

			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					CompanyFacade cFacade = (CompanyFacade) session.getClientFacade(); // Guaranteed by .web.WebConfig
					return ResponseEntity.ok(cFacade.getCompanyCoupons(price));

				} catch (BadNumberInputException e) {
					return ResponseEntity.badRequest().body("Invalid price.");
				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	//////////////////
	////////////////// Company
	//////////////////

	/**
	 * 
	 * @return Company entity, including coupons.
	 * @throws IllegalArgumentException
	 * @throws NoSuchElementException
	 */
	@PostMapping("/details/{token}")
	public ResponseEntity<Object> getDetails(@PathVariable String token) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					CompanyFacade cFacade = (CompanyFacade) session.getClientFacade(); // Guaranteed by .web.WebConfig
					cFacade.getCompanyDetails().setPassword("*********");
					return ResponseEntity.ok(cFacade.getCompanyDetails());

				} catch (NoSuchElementException e) {
					return ResponseEntity.badRequest().body("Company not found.");
				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");

	}

	@GetMapping("id/{token}")
	public ResponseEntity<Object> getId(@PathVariable String token) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					CompanyFacade cFacade = (CompanyFacade) session.getClientFacade(); // Guaranteed by .web.WebConfig
					return ResponseEntity.ok(String.valueOf(cFacade.getCompanyId()));

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}
}