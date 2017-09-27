package de.beuth.knabe.spring_ddd_bank.rest;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import multex.Exc;
import multex.Failure;
import multex.Msg;
import multex.Util;

/**
 * Centralized Exception Reporting for all Controller classes.
 * 
 * @see {@linkplain https://spring.io/guides/tutorials/bookmarks/#_building_a_hateoas_rest_service}
 */
@ControllerAdvice
public class ExceptionAdvice {

	private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);
	
	/** Message texts for exceptions. */
	private final ResourceBundle resourceBundle;

	@Autowired
	/**
	 * Constructs the advice with a bundle for localizable message texts for
	 * exceptions. At the moment the bundle is created as bean
	 * <code>messageTexts</code> in class
	 * {@link de.beuth.knabe.spring_ddd_bank.Application}.
	 */
	public ExceptionAdvice(final ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	@ResponseBody
	@ExceptionHandler({Exc.class, Failure.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	VndErrors reportException(final Exception ex) {
		//prepare messages for client:
		final StringBuffer out = new StringBuffer();
		multex.Msg.printMessages(out, ex, resourceBundle);
		final String messages = out.toString();
		
		//prepare log report with messages and stack trace:
		out.setLength(0);
		out.append("\nProcessing REST request threw exception:\n");
		out.append(messages);
		out.append(Util.lineSeparator);
		out.append(Msg.stackTraceFollows);
		out.append(Util.lineSeparator);
		Msg.printStackTrace(out, ex);
		
		//log the report on the server, and respond per messages to the client:
		log.error(out.toString());
		return new VndErrors("error", messages);
	}

}
