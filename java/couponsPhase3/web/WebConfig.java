package couponsPhase3.web;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import couponsPhase3.facade.AdminFacade;
import couponsPhase3.facade.ClientFacade;
import couponsPhase3.facade.CompanyFacade;
import couponsPhase3.facade.CustomerFacade;
import couponsPhase3.facade.SiteFacade;
import couponsPhase3.login.Session;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Wrappers for Controller classes to verify VERB requests are made by
 * appropriate sessions with valid UUID's. In addition the wrappers check for
 * session timeout.
 * 
 * @author D
 *
 */
@Configuration
@EnableSwagger2
@Aspect
public class WebConfig {

	/**
	 * TODO Remember to disable Swagger
	 * 
	 */
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build();
	}

	/**
	 * Inspect and validate sessions requesting HTTP Verbs
	 * 
	 */
	@Around("execution(* couponsPhase3.web.AdminController.*(..))")
	private ResponseEntity<?> authenticateAdmin(ProceedingJoinPoint point) throws Throwable {

		// For every Admin action; get token;
		String token = (String) point.getArgs()[0];

		// if the token is alive in sMap;
		if (sMap().containsKey(token)) {

			Session currentSession = sMap().get(token);

			ClientFacade admin = currentSession.getClientFacade();

			// check for timeout, and check session is indeed an Admin session
			if (admin.getClass() == AdminFacade.class // 1,800,000 == 30 minutes
					&& (System.currentTimeMillis() - currentSession.getLastAccessed()) < 1800000) {

				return (ResponseEntity<?>) point.proceed();

			} else { // else boot current session
				sMap().remove(token);
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized login");
	}

	/**
	 * Inspect and validate sessions requesting HTTP Verbs
	 * 
	 */
	@Around("execution(* couponsPhase3.web.CompanyController.*(..))")
	private ResponseEntity<?> authenticateCompany(ProceedingJoinPoint point) throws Throwable {

		// For every Company action; get token;
		String token = (String) point.getArgs()[0];

		// if the token is alive in sMap;
		if (sMap().containsKey(token)) {

			Session currentSession = sMap().get(token);

			ClientFacade company = currentSession.getClientFacade();

			// check for timeout, and check session is indeed a Company session
			if (company.getClass() == CompanyFacade.class // 1,800,000 == 30 minutes
					&& (System.currentTimeMillis() - currentSession.getLastAccessed()) < 1800000) {

				return (ResponseEntity<?>) point.proceed();

			} else { // else boot current session
				sMap().remove(token);
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized login");
	}

	/**
	 * Inspect and validate sessions requesting HTTP Verbs
	 * 
	 */
	@Around("execution(* couponsPhase3.web.CustomerController.*(..))")
	private ResponseEntity<?> authenticateCustomer(ProceedingJoinPoint point) throws Throwable {

		// For every Customer action; get token;
		String token = (String) point.getArgs()[0];

		// if the token is alive in sMap;
		if (sMap().containsKey(token)) {

			Session currentSession = sMap().get(token);

			ClientFacade customer = currentSession.getClientFacade();

			// check for timeout, and check session is indeed a Customer session
			if (customer.getClass() == CustomerFacade.class // 1,800,000 == 30 minutes
					&& (System.currentTimeMillis() - currentSession.getLastAccessed()) < 1800000) {

				return (ResponseEntity<?>) point.proceed();

			} else { // else boot current session
				sMap().remove(token);
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized login");
	}

	/**
	 * Log superuser actions; registering new users
	 */
	@Before("execution(* couponsPhase3.web.SiteController.*(..))")
	private void logAction(JoinPoint point) throws Throwable {

		// For every Site action; get token;
		String token = (String) point.getArgs()[0];
		if (token != null) {
			if (sMap().containsKey(token)) {
				// TODO create logger
				System.out.println("SITE ACTION START");
			}
			else {
				// TODO create logger
				System.out.println("BAD ATTEMPT @ BEFORE SiteController");}
		}
	}

	/**
	 * Inspect and validate sessions requesting HTTP Verbs
	 * 
	 */
	@Around("execution(* couponsPhase3.web.SiteController.*(..))")
	private ResponseEntity<?> authenticateSiteSuperuser(ProceedingJoinPoint point) throws Throwable {

		// For every Site action; get token;
		String token = (String) point.getArgs()[0];

		if (token != null)
			// if the token is alive in sMap;
			if (sMap().containsKey(token)) {

				Session currentSession = sMap().get(token);

				ClientFacade site = currentSession.getClientFacade();

				long t = System.currentTimeMillis() - currentSession.getLastAccessed();

				// check for timeout, and check session belongs to the site's superuser
				if (site.getClass() == SiteFacade.class // 6000 == 1 minute
						&& (t < 6000)) {

					return (ResponseEntity<?>) point.proceed();

				} else { // else boot current session

					sMap().remove(token);

					if (t >= 6000)
						return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("close");
				}
			}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized login");
	}

	/**
	 * Remove session after completed action
	 */
	@After("execution(* couponsPhase3.web.SiteController.*(..))")
	private void actionComplete(JoinPoint point) throws Throwable {

		// For every Site action; get token;
		String token = (String) point.getArgs()[0];

		if (token != null)
			if (sMap().containsKey(token))
				sMap().remove(token); // Action done - remove session

		// TODO create logger
		System.out.println("SITE ACTION END @ AFTER SiteController");
	}

	/**
	 * Maps UUID to facade
	 * 
	 * @return new HashMap
	 */
	@Bean
	public Map<String, Session> sMap() {
		return new HashMap<String, Session>();
	}
}