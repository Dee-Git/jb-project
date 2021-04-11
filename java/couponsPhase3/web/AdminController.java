package couponsPhase3.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import couponsPhase3.facade.AdminFacade;
import couponsPhase3.login.Session;
import couponsPhase3.tables.Company;
import couponsPhase3.tables.Customer;

//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
//@RequestMapping("admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

	@Autowired
	Map<String, Session> sMap;

	public AdminController() {
	}

	//
	// Check credentials
	//

	/**
	 * Check for session timeout, and for authorized (Admin) facade class. Will set
	 * session to null if true.
	 * 
	 * @param session: the current Session
	 * @param token:   UUID
	 */
	private void sessionCheck(String token, Session session) {

		if (session != null) {

			if ((System.currentTimeMillis() - session.getLastAccessed()) > 1800000
					|| session.getClientFacade().getClass() != AdminFacade.class) {
				// This is either a session timeout or an unauthorized action attempt
				sMap.remove(token);
				session = null;
			} else
				session.setLastAccessed(System.currentTimeMillis()); // Update session time
		}
	}

	/**
	 * Verifies the current session as an AdminSession, otherwise user will be re
	 * routed to /home
	 * 
	 * @param token: UUID
	 * @return OK if verified; else UNAUTHORIZED
	 */
	@GetMapping("/verify/{token}")
	public ResponseEntity<Object> validUserCheck(@PathVariable String token) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);
			if (session != null)
				return ResponseEntity.ok("Verified");
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/////////////////////////
	///////////////////////// Coupons
	/////////////////////////

	@GetMapping("/coupon/{token}")
	public ResponseEntity<Object> getAllCoupons(@PathVariable String token) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					AdminFacade aFacade = (AdminFacade) session.getClientFacade();
					return ResponseEntity.ok(aFacade.getAllCoupons());

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");

	}

	@DeleteMapping("/coupon/{token}/{id}")
	public ResponseEntity<Object> deleteCoupon(@PathVariable String token, @PathVariable int id) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					AdminFacade aFacade = (AdminFacade) session.getClientFacade();
					aFacade.deleteCoupon(id);
					return ResponseEntity.ok("Coupon deleted.");

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");

	}

	/////////////////////////
	///////////////////////// Companies
	/////////////////////////

	@PostMapping("/company/{token}")
	public ResponseEntity<Object> addCompany(@PathVariable String token, @RequestBody Company company) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				if (company == null)
					return ResponseEntity.badRequest().body("Bad request\nError 400");

				try {

					AdminFacade aFacade = (AdminFacade) session.getClientFacade();
					aFacade.addCompany(company);
					return ResponseEntity.ok(company);

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	@PutMapping("/company/{token}")
	public ResponseEntity<Object> updateCompany(@PathVariable String token, @RequestBody Company company) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				if (company == null)
					return ResponseEntity.badRequest().body("Bad request\nError 400");

				try {

					AdminFacade aFacade = (AdminFacade) session.getClientFacade();
					aFacade.updateCompany(company);
					return ResponseEntity.ok(company);

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	@DeleteMapping("/company/{token}/{id}")
	public ResponseEntity<Object> deleteCompany(@PathVariable String token, @PathVariable int id) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					AdminFacade aFacade = (AdminFacade) session.getClientFacade();
					aFacade.deleteCompany(id);
					return ResponseEntity.ok("Company id: " + id + " deleted");

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	@GetMapping("/company/{token}")
	public ResponseEntity<Object> getAllCompanies(@PathVariable String token) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					AdminFacade aFacade = (AdminFacade) session.getClientFacade();
					return ResponseEntity.ok(aFacade.getAllCompanies());

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");

	}

	@GetMapping("/company/{token}/{id}")
	public ResponseEntity<Object> getOneCompany(@PathVariable String token, @PathVariable int id) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					AdminFacade aFacade = (AdminFacade) session.getClientFacade();
					return ResponseEntity.ok(aFacade.getOneCompany(id));

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	/////////////////////////
	///////////////////////// Customers
	/////////////////////////

	@PostMapping("/customer/{token}")
	public ResponseEntity<Object> addCustomer(@PathVariable String token, @RequestBody Customer customer) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				if (customer == null)
					return ResponseEntity.badRequest().body("Bad request\nError 400");

				try {

					AdminFacade aFacade = (AdminFacade) session.getClientFacade();
					aFacade.addCustomer(customer);
					return ResponseEntity.ok(customer);

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	@PutMapping("/customer/{token}")
	public ResponseEntity<Object> updateCustomer(@PathVariable String token, @RequestBody Customer customer) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				if (customer == null)
					return ResponseEntity.badRequest().body("Bad request\nError 400");

				try {

					AdminFacade aFacade = (AdminFacade) session.getClientFacade();
					aFacade.updateCustomer(customer);
					return ResponseEntity.ok(customer);

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	@DeleteMapping("/customer/{token}/{id}")
	public ResponseEntity<Object> deleteCustomer(@PathVariable String token, @PathVariable int id) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					AdminFacade aFacade = (AdminFacade) session.getClientFacade();
					aFacade.deleteCustomer(id);
					return ResponseEntity.ok("Customer id: " + id + " deleted");

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	@GetMapping("/customer/{token}")
	public ResponseEntity<Object> getAllCustomers(@PathVariable String token) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					AdminFacade aFacade = (AdminFacade) session.getClientFacade();
					return ResponseEntity.ok(aFacade.getAllCustomers());

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}

	@GetMapping("/customer/{token}/{id}")
	public ResponseEntity<Object> getOneCustomer(@PathVariable String token, @PathVariable int id) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				try {

					AdminFacade aFacade = (AdminFacade) session.getClientFacade();
					return ResponseEntity.ok(aFacade.getOneCustomer(id));

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}
}