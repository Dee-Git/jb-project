package couponsPhase3.web;

import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import couponsPhase3.exceptions.BadClientTypeException;
import couponsPhase3.exceptions.BadLoginException;
import couponsPhase3.facade.ClientFacade;
import couponsPhase3.login.LoginBean;
import couponsPhase3.login.LoginManager;
import couponsPhase3.login.Session;

import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class LoginController {

	@Autowired
	private LoginManager loginManager;

	@Autowired
	private Map<String, Session> sMap;

	public LoginController() {
	}

	/**
	 * Generate UUID and Client Session and put in session Map (sMap)
	 * 
	 * @param /login/ Body containing PW, eMail, ClientType
	 * @return .ok(token) or .badRequest(exception)
	 * @throws BadLoginException
	 * @throws BadClientTypeException
	 */
	@PostMapping("/login/")
	public ResponseEntity<Object> login(@RequestBody LoginBean login) {

		if (login == null)
			return ResponseEntity.badRequest().body("Bad request\nError 400");

		try {

			ClientFacade cFacade = loginManager.login(login.getEmail(), login.getPassword(), login.getType());

			Session session = new Session(cFacade, System.currentTimeMillis());

			String token = UUID.randomUUID().toString();

			sMap.put(token, session);

			return ResponseEntity.ok(token);

		} catch (BadLoginException e) {
			return ResponseEntity.badRequest().body("Login Error: " + e.getMessage());
		} catch (BadClientTypeException e) {
			return ResponseEntity.badRequest().body("Login Error: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
	}

	@GetMapping("/logout/{token}")
	public void logout(@PathVariable String token) {
		if (token != null)
			sMap.remove(token);
	}
}