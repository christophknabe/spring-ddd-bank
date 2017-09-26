package de.beuth.knabe.spring_ddd_bank.rest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import multex.Exc;

import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;

/**Centralized Exception Reporting for all Controller classes.
 * @see {@linkplain https://spring.io/guides/tutorials/bookmarks/#_building_a_hateoas_rest_service}
 */
@ControllerAdvice
public class ExceptionAdvice {
	
	/**Message texts for exceptions.*/
	final ResourceBundle resourceBundle;
	
	@Autowired
	public ExceptionAdvice(final ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	@ResponseBody
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	VndErrors excExceptionHandler(final Exc ex) {
		final StringBuffer messages = new StringBuffer();
		multex.Msg.printMessages(messages, ex, resourceBundle);
		return new VndErrors("error", messages.toString());
	}

}
