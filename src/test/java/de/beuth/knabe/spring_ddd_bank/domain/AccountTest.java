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
		{
			final Account result = new Account("Lisa's Savings");
			assertEquals(null, result.getId());
			assertEquals("Lisa's Savings", result.getName());
			assertEquals(Amount.ZERO, result.getBalance());
			assertEquals("Account{accountNo=, name='Lisa's Savings', balance='0,00'}", result.toString());
			try {
				result.accountNo();
				fail("Account.NotYetSavedExc expected");
			} catch (Account.NotYetSavedExc expected) {				
			}
		}
	}
	
	@Test
	public void constructExtractAccountNo() {
		{
			final AccountNo result = new AccountNo(Long.MAX_VALUE);
			assertEquals(Long.MAX_VALUE, result.toLong());
			assertEquals(Long.toString(Long.MAX_VALUE), result.toString());
		}
		{
			final AccountNo result = new AccountNo("1");
			assertEquals(1L, result.toLong());
			assertEquals(Long.toString(1L), result.toString());
		}
		
	}

	@Test
	public void constructIllegalAccountNos() {
		try {
			new AccountNo((Long)null);
			fail("AccountNo.IllegalExc expected");
		} catch (AccountNo.IllegalExc expected) {
		}
		try {
			new AccountNo((String)null);
			fail("AccountNo.IllegalExc expected");
		} catch (AccountNo.IllegalExc expected) {
		}
		try {
			new AccountNo("");
			fail("AccountNo.IllegalExc expected");
		} catch (AccountNo.IllegalExc expected) {
		}
		try {
			new AccountNo("A");
			fail("AccountNo.IllegalExc expected");
		} catch (AccountNo.IllegalExc expected) {
		}
		try {
			new AccountNo(".");
			fail("AccountNo.IllegalExc expected");
		} catch (AccountNo.IllegalExc expected) {
		}
	}
	
}
