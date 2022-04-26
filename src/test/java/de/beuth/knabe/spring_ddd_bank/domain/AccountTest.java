package de.beuth.knabe.spring_ddd_bank.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

/** Test driver for the entity object {@link Account} */
public class AccountTest {

	@Before
	public void setUp() {
		Locale.setDefault(Locale.GERMANY);
	}

	@Test
	public void constructExtract() {
		final Account result = new Account("Lisa's Savings");
		thenHasGivenNameAndNoIdAndZeroBalance(result);
	}

	private void thenHasGivenNameAndNoIdAndZeroBalance(Account result) {
		@SuppressWarnings("deprecation") //Should be used only in domain model
		final Long id = result.getId();
		assertEquals(null, id);
		assertEquals("Lisa's Savings", result.getName());
		assertEquals(Amount.ZERO, result.getBalance());
		assertEquals("Account{accountNo=NotYetSaved, name='Lisa's Savings', balance='0,00'}", result.toString());
		try {
			result.accountNo();
			fail("Account.NotYetSavedExc expected");
		} catch (Account.NotYetSavedExc expected) {
		}
	}
	
}
