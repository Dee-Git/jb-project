package couponsPhase3.web;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import couponsPhase3.exceptions.BadCategoryTypeException;
import couponsPhase3.exceptions.BadCouponException;
import couponsPhase3.exceptions.BadNumberInputException;
import couponsPhase3.exceptions.CouponNotFoundException;
import couponsPhase3.exceptions.DoubleDipException;
import couponsPhase3.exceptions.OutOfCouponsException;
import couponsPhase3.facade.CustomerFacade;
import couponsPhase3.login.Session;
import couponsPhase3.tables.Category;
import couponsPhase3.tables.Coupon;

import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("customer")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

	@Autowired
	Map<String, Session> sMap;

	public CustomerController() {
	}

	//
	// Check credentials
	//

	/**
	 * Check for session timeout, and for authorized (Customer) facade class. Will
	 * set session to null if true.
	 * 
	 * @param session: the current Session
	 * @param token:   UUID
	 */
	private void sessionCheck(String token, Session session) {

		if (session != null) {

			if ((System.currentTimeMillis() - session.getLastAccessed()) > 1800000
					|| session.getClientFacade().getClass() != CustomerFacade.class) {
				// This is either a session timeout or an unauthorized action attempt
				sMap.remove(token);
				session = null;
			} else
				session.setLastAccessed(System.currentTimeMillis()); // Update session time
		}
	}

	//////////////////
	////////////////// Purchases
	//////////////////

	/**
	 * Add a purchase of coupon.id for associated customer id. Coupon ID must
	 * already exist in database.
	 * 
	 * @param a VALID coupon of Coupon class
	 * @return true on success; otherwise false.
	 * @throws BadCouponException
	 * @throws OutOfCouponsException
	 * @throws CouponNotFoundException
	 * @throws DoubleDipException
	 * @throws BadNumberInputException
	 * @throws IllegalArgumentException
	 * @throws EmptyResultDataAccessException
	 */
	@PostMapping("/coupon/purchase/{token}")
	public ResponseEntity<Object> purchaseCoupon(@PathVariable String token, @RequestBody Coupon coupon) {

		if (token != null) {

			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				if (coupon == null)
					return ResponseEntity.badRequest().body("Bad request\nError 400");

				try {

					CustomerFacade cFacade = (CustomerFacade) session.getClientFacade();
					cFacade.purchaseCoupon(coupon);
					return ResponseEntity.ok("Coupon purchased.");

				} catch (OutOfCouponsException e) {
					return ResponseEntity.badRequest().body("We're sorry, this coupon is out of stock.");
				} catch (DoubleDipException e) {
					return ResponseEntity.badRequest().body("You've already purchased this coupon.");
				} catch (BadNumberInputException e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				} catch (CouponNotFoundException e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				} catch (BadCouponException e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/**
	 * List all coupons.
	 * 
	 * @return List of all Coupons found, or empty list.
	 */
	@GetMapping("/coupon/all/{token}")
	public ResponseEntity<Object> listAllCoupons(@PathVariable String token) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					CustomerFacade cFacade = (CustomerFacade) session.getClientFacade();
					return ResponseEntity.ok(cFacade.getAllCoupons());

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/**
	 * List all coupons in {category}.
	 * 
	 * @return List of all Coupons found, or empty list.
	 * @throws BadCategoryTypeException for invalid enum
	 */
	@GetMapping("/coupon/allcategory/{category}/{token}")
	public ResponseEntity<Object> listCategoryCoupons(@PathVariable String token, @PathVariable Category category) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				if (category == null)
					return ResponseEntity.badRequest().body("Invalid category");

				try {

					CustomerFacade cFacade = (CustomerFacade) session.getClientFacade();
					return ResponseEntity.ok(cFacade.getCoupons(category));

				} catch (BadCategoryTypeException e) {
					return ResponseEntity.badRequest().body("Invalid category");
				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/**
	 * List all coupons under or equal to {price}.
	 * 
	 * @return List of all Coupons found, or empty list.
	 * @throws BadNumberInputException if maxPrice negative or zero
	 */
	@GetMapping("/coupon/allprice/{price}/{token}")
	public ResponseEntity<Object> listUnderPriceCoupons(@PathVariable String token, @PathVariable Double price) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					CustomerFacade cFacade = (CustomerFacade) session.getClientFacade();
					return ResponseEntity.ok(cFacade.getCoupons(price));

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	//////////////////
	////////////////// Customer details
	//////////////////

	/**
	 * Get all coupons associated with customer ID. Will throw if for some reason
	 * customerId is invalid.
	 * 
	 * @return Set of coupons, or empty set.
	 * @throws NoSuchElementException
	 * @throws IllegalArgumentException
	 */
	@GetMapping("my/coupon/all/{token}")
	public ResponseEntity<Object> getAllCoupons(@PathVariable String token) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					CustomerFacade cFacade = (CustomerFacade) session.getClientFacade();
					return ResponseEntity.ok(cFacade.getCustomerCoupons());

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/**
	 * Get all coupons (in category) associated with customer ID. Will throw if for
	 * some reason customerId is invalid.
	 * 
	 * @param enum Category.value
	 * @return Set of coupons, or empty set.
	 * @throws BadCategoryTypeException
	 * @throws NoSuchElementException
	 */
	@GetMapping("my/coupon/allcategory/{category}/{token}")
	public ResponseEntity<Object> getAllCoupons(@PathVariable String token, @PathVariable String category) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				if (category == null)
					return ResponseEntity.badRequest().body("Invalid category");

				try {

					CustomerFacade cFacade = (CustomerFacade) session.getClientFacade();
					return ResponseEntity.ok(cFacade.getCustomerCoupons(Category.valueOf(category)));

				} catch (BadCategoryTypeException e) {
					return ResponseEntity.badRequest().body("Invalid category");
				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/**
	 * Get all coupons (under or equal to maxPrice) associated with customer ID.
	 * Will throw if for some reason customerId is invalid.
	 * 
	 * @param coupon max price
	 * @return Set of coupons, or empty set.
	 * @throws BadNumberInputException
	 * @throws NoSuchElementException
	 */
	@GetMapping("my/coupon/allprice/{maxPrice}/{token}")
	public ResponseEntity<Object> getAllCoupons(@PathVariable String token, @PathVariable double maxPrice) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					CustomerFacade cFacade = (CustomerFacade) session.getClientFacade();
					return ResponseEntity.ok(cFacade.getCustomerCoupons(maxPrice));

				} catch (BadNumberInputException e) {
					return ResponseEntity.badRequest().body("Invalid price");
				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/**
	 * 
	 * @return Customer entity, including coupons.
	 * @throws IllegalArgumentException
	 * @throws NoSuchElementException
	 */
	@PostMapping("my/details/{token}")
	public ResponseEntity<Object> getDetails(@PathVariable String token) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					CustomerFacade cFacade = (CustomerFacade) session.getClientFacade(); // Guaranteed by .web.WebConfig
					cFacade.getCustomerDetails().setPassword("*********");
					return ResponseEntity.ok(cFacade.getCustomerDetails());

				} catch (NoSuchElementException e) {
					return ResponseEntity.badRequest().body("Company not found");
				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	@GetMapping("my/id/{token}")
	public ResponseEntity<String> getId(@PathVariable String token) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					CustomerFacade cFacade = (CustomerFacade) session.getClientFacade();
					return ResponseEntity.ok(String.valueOf(cFacade.getCustomerId()));

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}
}