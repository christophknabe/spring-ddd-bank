package de.beuth.knabe.spring_ddd_bank.rest_interface;

import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import multex.Exc;
import multex.Failure;
import multex.Msg;

/**
 * Centralized Exception Reporting for all Controller classes.
 * 
 * @see <a href="https://spring.io/guides/tutorials/bookmarks/#_building_a_hateoas_rest_service">Building a HATEOAS REST Service</a> with Spring.
 */
@ControllerAdvice
public class ExceptionAdvice {


	/**The baseName for locating the exception message text resource bundle.*/ 
	public static final String BASE_NAME = "MessageText";

	private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

	@ResponseBody
	@ExceptionHandler({Exception.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	/**Reports the given Exception with messages localized according to the given Locale of the web request.*/
	VndErrors reportException(final Exception ex, final Locale requestLocale) {
		//prepare messages for client with the Locale of the request:
		/** Message texts for exceptions. */
		final ResourceBundle requestResourceBundle = ResourceBundle.getBundle(BASE_NAME, requestLocale);
		final StringBuffer clientMessages = new StringBuffer();
		multex.Msg.printMessages(clientMessages, ex, requestResourceBundle);
		final String clientMesagesString = clientMessages.toString();

		//prepare log report with messages and stack trace:
		final StringBuffer serverMessages = new StringBuffer();
		serverMessages.append("Processing REST request threw exception:\n");
		final Locale defaultLocale = Locale.getDefault();
		final ResourceBundle defaultResourceBundle = ResourceBundle.getBundle(BASE_NAME, defaultLocale);
		if(!defaultResourceBundle.equals(requestResourceBundle)) {
			serverMessages.append(clientMesagesString);
			serverMessages.append("\n-----\n");
		}
		Msg.printReport(serverMessages, ex, defaultResourceBundle);
		
		//log the report on the server:
		log.error(serverMessages.toString());
		//respond with localized messages to the client:
		return new VndErrors("error", clientMesagesString);
	}
}
