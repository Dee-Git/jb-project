package couponsPhase3.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import couponsPhase3.exceptions.BadClientTypeException;
import couponsPhase3.exceptions.BadLoginException;
import couponsPhase3.facade.AdminFacade;
import couponsPhase3.facade.ClientFacade;
import couponsPhase3.facade.CompanyFacade;
import couponsPhase3.facade.CustomerFacade;
import couponsPhase3.facade.SiteFacade;
import couponsPhase3.tables.ClientType;

/**
 * Handles login requests made by LoginController and returns an appropriate
 * facade.
 * 
 * @author D
 *
 */
@Component
@Scope("singleton")
public class LoginManager {

	@Autowired
	private AdminFacade adminFacade;
	@Autowired
	private SiteFacade siteFacade;
	@Autowired
	private CompanyFacade companyFacade;
	@Autowired
	private CustomerFacade customerFacade;

	/**
	 * Calls facade.*.login(email, password) according to clientType
	 * 
	 * @param email
	 * @param password
	 * @param clientType
	 * @return an appropriate facade with set id
	 * @throws BadLoginException      if login failed
	 * @throws BadClientTypeException for invalid enum
	 */
	public ClientFacade login(String email, String password, ClientType clientType)
			throws BadLoginException, BadClientTypeException {

		email = email.strip();
		password = password.strip();

		if (!(ClientType.class == clientType.getClass()))
			throw new BadClientTypeException();

		switch (clientType) {

		case Administrator: {

			try {

				if (adminFacade.login(email, password) == Integer.MAX_VALUE)
					return adminFacade;

			} catch (Exception e) {
				throw new BadLoginException();
			}
		}

		case Site: {

			try {

				if (siteFacade.login(email, password) == Integer.MAX_VALUE)
					return siteFacade;

			} catch (Exception e) {
				throw new BadLoginException();
			}
		}

		case Company: {

			try {

				CompanyFacade com = companyFacade;
				com.setCompanyId(companyFacade.login(email, password));
				return com;

			} catch (Exception e) {
				throw new BadLoginException();
			}
		}

		case Customer: {

			try {

				CustomerFacade cus = customerFacade;
				cus.setCustomerId(customerFacade.login(email, password));
				return cus;

			} catch (Exception e) {
				throw new BadLoginException();
			}
		}

		default:
			throw new BadClientTypeException();

		}

	}
}