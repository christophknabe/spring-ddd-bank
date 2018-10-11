package de.beuth.knabe.spring_ddd_bank.rest_interface;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import de.beuth.knabe.spring_ddd_bank.domain.BankService;
import de.beuth.knabe.spring_ddd_bank.domain.Client;
import multex.Msg;

/**
 * Centralized Exception Reporting for all Controller classes.
 * 
 * @see <a href=
 *      "https://spring.io/guides/tutorials/bookmarks/#_building_a_hateoas_rest_service"
 *      > Building a HATEOAS REST Service</a> with Spring.
 * @see <a href=
 *      "https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc" 
 *      > Exception Handling in Spring MVC</a>.
 * @author Christoph Knabe
 */
@ControllerAdvice
public class ExceptionAdvice {

	/** The baseName for locating the exception message text resource bundle. */
	public static final String BASE_NAME = "MessageText";

	private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

	@ResponseBody
	@ExceptionHandler({ Exception.class })
	/**
	 * Reports the given Exception with messages in three ways:
	 * <ol>
	 * <li>with messages in default language and with stack trace to the error
	 * log</li>
	 * <li>with localized messages according to the given Locale of the web request
	 * to the REST client</li>
	 * <li>as HTTP status code to the REST client</li>
	 * </ol>
	 */
	VndErrors reportException(final Exception ex, final Locale requestLocale, final HttpServletResponse response) {
		// prepare messages for REST client with the Locale of the request:
		/** Message texts for exceptions. */
		final ResourceBundle requestResourceBundle = ResourceBundle.getBundle(BASE_NAME, requestLocale);
		final StringBuffer clientMessages = new StringBuffer();
		multex.Msg.printMessages(clientMessages, ex, requestResourceBundle);
		final String clientMesagesString = clientMessages.toString();

		// prepare log report with messages and stack trace:
		final StringBuffer serverMessages = new StringBuffer();
		serverMessages.append("Processing REST request threw exception:\n");
		final Locale defaultLocale = Locale.getDefault();
		final ResourceBundle defaultResourceBundle = ResourceBundle.getBundle(BASE_NAME, defaultLocale);
		if (!defaultResourceBundle.equals(requestResourceBundle)) {
			serverMessages.append(clientMesagesString);
			serverMessages.append("\n-----\n");
		}
		Msg.printReport(serverMessages, ex, defaultResourceBundle);

		// log the report on the server:
		log.error(serverMessages.toString());
		// respond with localized messages to the client:
		response.setStatus(exceptionToStatus(ex).value());
		return new VndErrors("error", clientMesagesString);
	}

	final String restInterfacePackagePrefix = _computePackagePrefix(ApplicationController.ClientCreateWithIdExc.class);
	final String domainPackagePrefix = _computePackagePrefix(Client.NotOwnerExc.class);

	/**
	 * Converts the given exception to the most suiting HTTP response status.
	 * 
	 * @return one of HttpStatus.NOT_FOUND, HttpStatus.FORBIDDEN,
	 *         HttpStatus.BAD_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR
	 * @param exc
	 *            the exception to be converted
	 */
	HttpStatus exceptionToStatus(final Exception exc) {
		if (exc instanceof BankService.ClientNotFoundExc) {
			return HttpStatus.NOT_FOUND;
		}
		if (exc instanceof Client.NotManagedAccountExc) {
			return HttpStatus.NOT_FOUND;
		}
		if (exc instanceof Client.NotOwnerExc) {
			return HttpStatus.FORBIDDEN;
		}
		if (exc instanceof Client.WithoutRightExc) {
			return HttpStatus.FORBIDDEN;
		}
		final String excClassName = exc.getClass().getName();
		if (excClassName.startsWith(restInterfacePackagePrefix) || excClassName.startsWith(domainPackagePrefix)) {
			return HttpStatus.BAD_REQUEST;
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	/**
	 * Returns the name prefix of all classes in the package of the given exception
	 * class.
	 * 
	 * @return if the given class is modeled in the package
	 *         <code>tld.mysoftware.domain</code>, the result will be
	 *         <code>tld.mysoftware.domain.</code> with trailing dot.
	 * @param excClass
	 *            a Class object for a class in a package
	 */
	String _computePackagePrefix(final Class<?> excClass) {
		final String packageName = excClass.getPackage().getName();
		return packageName + '.';
	}

}
