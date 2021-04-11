package couponsPhase3.web;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import couponsPhase3.facade.SiteFacade;
import couponsPhase3.login.Session;
import couponsPhase3.tables.Customer;

@RestController
//@RequestMapping("site")
@CrossOrigin(origins = "http://localhost:4200")
public class SiteController {

	@Autowired
	Map<String, Session> sMap;

	/**
	 * Check for session timeout, and for authorized (Admin) facade class. Will set
	 * session to null if true.
	 * 
	 * @param session: the current Session
	 * @param token:   UUID
	 */
	private void sessionCheck(String token, Session session) {

		if (session != null) {

			if ((System.currentTimeMillis() - session.getLastAccessed()) > 6000
					|| session.getClientFacade().getClass() != SiteFacade.class) {
				// This is either a session timeout or an unauthorized action attempt
				sMap.remove(token);
				session = null;
			}
		}
	}

	@PostMapping("/register/{token}")
	public ResponseEntity<Object> register(@PathVariable String token, @RequestBody Customer newCustomer) {

		if (token != null) {
			Session session = sMap.get(token);
			sessionCheck(token, session);

			if (session != null) {

				if (newCustomer == null)
					return ResponseEntity.badRequest().body("Bad request\nError 400");

				try {

					SiteFacade sFacade = (SiteFacade) session.getClientFacade();
					sFacade.registerCustomer(newCustomer);
					return ResponseEntity.ok(newCustomer);

				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized action");
	}
}
