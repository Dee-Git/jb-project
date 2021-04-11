package couponsPhase3.web;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class MyErrorController implements ErrorController {

	@RequestMapping("/error")
	public String handleError(HttpServletRequest request) {

		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		
		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());

			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				return "<!DOCTYPE html>\r\n" + 
						"<html>\r\n" + 
						"<body>\r\n" + 
						"<h1>This page doesn't exist</h1>\r\n" + 
						"<h2>Go back to the last loaded page</h2>\r\n" + 
						"</body>\r\n" + 
						"</html>";
			} else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				return "<!DOCTYPE html>\r\n" + 
						"<html>\r\n" + 
						"<body>\r\n" + 
						"<h1>Something went wrong! </h1>\r\n" + 
						"<h2>Go back to the last loaded page</h2>\r\n" + 
						"</body>\r\n" + 
						"</html>";
			}
			else
				return HttpStatus.valueOf(statusCode).toString();
		}
		
		System.out.println("Unspecifed Error in MyErrorController; No ERROR_STATUS_CODE");
		
		return "Unspecified error.\n\nPlease return to the last page.";
	}

	@Override
	public String getErrorPath() {

		return "/error";
	}
}