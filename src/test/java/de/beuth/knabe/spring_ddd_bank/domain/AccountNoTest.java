package de.beuth.knabe.spring_ddd_bank.domain;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/** Test driver for the value object {@link AccountNo} */
public class AccountNoTest {

	@Before
	public void setUp() {
		Locale.setDefault(Locale.GERMANY);
	}

	@Test
	public void constructExtract() {
		{
			final AccountNo result = new AccountNo(0L);
			thenWraps(result, 0L);
		}
		{
			final AccountNo result = new AccountNo(Long.MAX_VALUE);
			thenWraps(result, Long.MAX_VALUE);
		}
		{
			final AccountNo result = new AccountNo("0");
			thenWraps(result, 0L);
		}
		{
			final AccountNo result = new AccountNo(Long.toString(Long.MAX_VALUE));
			thenWraps(result, Long.MAX_VALUE);
		}
	}

	private void thenWraps(AccountNo result, long content) {
		assertEquals(content, result.toLong());
		assertEquals(Long.toString(content), result.toString());
	}

	@Test
	public void constructIllegals() {
		try {
			new AccountNo((Long)null);
			fail("AccountNo.IllegalExc expected");
		} catch (AccountNo.IllegalExc expected) {
		}
		try {
			new AccountNo(Long.valueOf(-1));
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

	@Test
	public void hashCodeShouldBeConsistentToEquals(){
		final var aLong = getRandomLong();
		final var a = new AccountNo(aLong);
		final var bLong = getRandomLong();
		final var b = new AccountNo(bLong);
		if(aLong==bLong){
			assertEquals(a.hashCode(), b.hashCode());
			assertTrue(a.equals(b));
			assertTrue(b.equals(a));
		}else{
			assertNotEquals(a.hashCode(), b.hashCode());
			assertFalse(a.equals(b));
			assertFalse(b.equals(a));
		}
	}

	@Test
	public void equalsShouldRefuseNull(){
		final var accountNo = new AccountNo(getRandomLong());
		assertEquals(false, accountNo.equals(null));
	}

	@Test
	public void equalsShouldRefuseOtherClass(){
		final var accountNo = new AccountNo(getRandomLong());
		assertEquals(false, accountNo.equals("dummy"));
	}

	private long getRandomLong() {
		final var result = Math.round(Math.random() * 1E18 - 0.5);
		return result;
	}

}
