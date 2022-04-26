package de.beuth.knabe.spring_ddd_bank.domain;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.Assert.*;

/** Test driver for the entity linkage object {@link AccountAccess} */
public class AccountAccessTest {

	@Before
	public void setUp() {
		Locale.setDefault(Locale.GERMANY);
	}

	@Test
	public void constructExtract() {
		//GIVEN
		final Client client = new Client("jack", LocalDate.parse("1992-12-31"));
		final Account account = new Account("Jack's Savings");
		//WHEN
		final AccountAccess result = new AccountAccess(client, true, account);
		//THEN
		assertSame(client, result.getClient());
		assertEquals(true, result.isOwner());
		assertSame(account, result.getAccount());
		final String clientString = client.toString();
		final String accountString = account.toString();
		assertEquals("AccountAccess{client=" + clientString + ", isOwner=true, account=" + accountString + "}", result.toString());
	}
	
}
