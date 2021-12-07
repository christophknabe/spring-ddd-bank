package de.beuth.knabe.spring_ddd_bank.rest_interface;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import de.beuth.knabe.spring_ddd_bank.domain.Amount;
import de.beuth.knabe.spring_ddd_bank.domain.BankService;
import de.beuth.knabe.spring_ddd_bank.domain.Client;
import de.beuth.knabe.spring_ddd_bank.rest_interface.ApplicationController.ClientCreateWithIdExc;

/** Test driver for the {@link ExceptionAdvice} */
public class ExceptionAdviceTest {

	@Autowired
	private ExceptionAdvice testee = new ExceptionAdvice();

	@Before
	public void cleanUp() {
		Locale.setDefault(Locale.GERMANY);
	}

	@Test
	public void computePackagePrefix() {
		assertEquals(testee._computePackagePrefix(ClientCreateWithIdExc.class),
				"de.beuth.knabe.spring_ddd_bank.rest_interface.");
		assertEquals(testee._computePackagePrefix(Client.NotOwnerExc.class), "de.beuth.knabe.spring_ddd_bank.domain.");
		assertEquals(testee.restInterfacePackagePrefix, "de.beuth.knabe.spring_ddd_bank.rest_interface.");
		assertEquals(testee.domainPackagePrefix, "de.beuth.knabe.spring_ddd_bank.domain.");
	}

	@Test
	public void exceptionToStatus() {
		// Specific business rule violations:
		_assertStatus(HttpStatus.NOT_FOUND, new BankService.ClientNotFoundExc());
		_assertStatus(HttpStatus.NOT_FOUND, new Client.NotManagedAccountExc());
		_assertStatus(HttpStatus.FORBIDDEN, new Client.NotOwnerExc());
		_assertStatus(HttpStatus.FORBIDDEN, new Client.WithoutRightExc());

		// General business rule violations:
		_assertStatus(HttpStatus.BAD_REQUEST, new Amount.RangeExc());
		_assertStatus(HttpStatus.BAD_REQUEST, new BankService.DeleteExc());
		_assertStatus(HttpStatus.BAD_REQUEST, new BankService.UsernameExc());
		_assertStatus(HttpStatus.BAD_REQUEST, new Client.AmountExc());
		_assertStatus(HttpStatus.BAD_REQUEST, new Client.DoubleManagerExc());
		_assertStatus(HttpStatus.BAD_REQUEST, new Client.MinimumBalanceExc());

		// Rule violations in the rest-interface layer:
		_assertStatus(HttpStatus.BAD_REQUEST, new ClientCreateWithIdExc());

		// other errors:
		_assertStatus(HttpStatus.INTERNAL_SERVER_ERROR, new IllegalAccessException());
	}

	/**
	 * Asserts that the given exception will be converted to the given HTTP status.
	 * 
	 * @param httpStatus
	 *            the expected HTTP status
	 * @param exc
	 *            exception object
	 */
	private void _assertStatus(final HttpStatus httpStatus, final Exception exc) {
		assertEquals(exc.toString(), httpStatus.name(), testee.exceptionToStatus(exc).name());
	}

}
