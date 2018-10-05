/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.beuth.knabe.spring_ddd_bank.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.beuth.knabe.spring_ddd_bank.domain.Client.NotManagedAccountExc;

/** Test driver for the Rich Domain Object {@linkplain Client} */
@RunWith(SpringRunner.class)
// @DataJpaTest
// @AutoConfigureTestDatabase
@SpringBootTest
public class ClientTest {

	/** Only for use in the cleanUp method! */
	@Autowired
	private CleanupService cleanupService;

	@Autowired
	private BankService bankService;

	@Before
	public void cleanUp() {
		cleanupService.deleteAll();
		Locale.setDefault(Locale.GERMANY);
	}

	@Test
	public void createAccountCheckProperties() {
		final Client jack = bankService.createClient("jack", LocalDate.parse("1966-12-31"));
		final AccountAccess jacksSavings = jack.createAccount("Jack's Savings");
		assertNotNull(jacksSavings);
		assertEquals(jack, jacksSavings.getClient());
		assertEquals("jack", jacksSavings.getClient().getUsername());
		assertEquals(true, jacksSavings.isOwner());
		final Account jacksSavingsAccount = jacksSavings.getAccount();
		assertEquals("Jack's Savings", jacksSavingsAccount.getName());
		assertEquals(0.0, jacksSavingsAccount.getBalance().toDouble(), 0.001);
		assertEquals(jack.getUsername(), jacksSavings.getClient().getUsername());
		assertEquals(true, jack.sameIdentityAs(jacksSavings.getClient()));
		final String report = jack.accountsReport();
		final AccountNo jacksSavingsAccountNo = jacksSavingsAccount.accountNo();
		assertEquals("Accounts of client: jack\n" + jacksSavingsAccountNo + "\tisOwner\t 0,00\tJack's Savings\n", report);
	}

	@Test
	public void findAndCreateAccount() {
		final String jackUsername = "jack";
		final Client jack = bankService.createClient(jackUsername, LocalDate.parse("1966-12-31"));
		// Before createAccount no accounts can be found:
		final String noAccounts = jack.accountsReport();
		assertEquals("Accounts of client: jack\n", noAccounts);
		try {
			jack.findMyAccount(new AccountNo(1L));
			fail("Client.NotManagedAccountExc expected");
		} catch (Client.NotManagedAccountExc expected) {
		}

		// Create an Account:
		final AccountAccess jacksSavingsAccess = jack.createAccount("Jack's Savings");
		assertNotNull("Jack's Savings", jacksSavingsAccess);

		// By createAccount a new Account object was created with an accountNo:
		final Account jacksSavingsAccount = jacksSavingsAccess.getAccount();
		final AccountNo jacksSavingsAccountNo = jacksSavingsAccount.accountNo();
		assertNotNull("jacksSavingsAccountNo", jacksSavingsAccountNo);

		// We can retrieve the Account by its accountNo:
		final Account account = jack.findMyAccount(jacksSavingsAccountNo);
		assertNotNull(account);
		assertEquals(jacksSavingsAccountNo, account.accountNo());
	}
	
	@Test
	public void findNotMyAccount() {
		//Create Jack and his account:
		final String jackUsername = "jack";
		final Client jack = bankService.createClient(jackUsername, LocalDate.parse("1966-12-31"));
		final AccountAccess jacksSavingsAccess = jack.createAccount("Jack's Savings");
		//Create Anna and her account:
		final String annaUsername = "jack";
		final Client anna = bankService.createClient(annaUsername, LocalDate.parse("1970-01-01"));
		final AccountAccess annasGiroAccess = anna.createAccount("Anna's Giro");
		//Check that nobody can access an Account of another person:
		try {
			jack.findMyAccount(annasGiroAccess.getAccount().accountNo());
			fail("NotManagedAccountExc expected");
		}catch(NotManagedAccountExc expected) {}
		try {
			anna.findMyAccount(jacksSavingsAccess.getAccount().accountNo());
			fail("NotManagedAccountExc expected");
		}catch(NotManagedAccountExc expected) {}		
	}

	@Test
	public void deposit() {
		final Client jack = bankService.createClient("jack", LocalDate.parse("1966-12-31"));
		final AccountAccess jacksGiro = jack.createAccount("Jack's Giro");
		final AccountNo accountNo = jacksGiro.getAccount().accountNo();
		jack.deposit(accountNo, new Amount(999999999, 99));
		final String report = jack.accountsReport();
		assertEquals("Accounts of client: jack\n" + accountNo + "\tisOwner\t999999999,99\tJack's Giro\n", report);
	}

	@Test
	public void depositAmountExc() {
		final Client jack = bankService.createClient("jack", LocalDate.parse("1966-12-31"));
		final Account jacksGiro = jack.createAccount("Jack's Giro").getAccount();
		final AccountNo accountNo = jacksGiro.accountNo();
		try {
			jack.deposit(accountNo, Amount.ZERO);
			fail("Client.AmountExc expected");
		} catch (Client.AmountExc expected) {
		}
		final String report = jack.accountsReport();
		assertEquals("Accounts of client: jack\n" + accountNo + "\tisOwner\t 0,00\tJack's Giro\n", report);
	}

	@Test
	public void depositsReport() {
		final Client jack = bankService.createClient("jack", LocalDate.parse("1966-12-31"));
		final AccountAccess jacksGiro = jack.createAccount("Jack's Giro");
		final AccountAccess jacksSavings = jack.createAccount("Jack's Savings");
		final AccountNo jacksGiroAccountNo = jacksGiro.getAccount().accountNo();
		jack.deposit(jacksGiroAccountNo, new Amount(999999999, 99));
		final AccountNo jacksSavingsAccountNo = jacksSavings.getAccount().accountNo();
		jack.deposit(jacksSavingsAccountNo, new Amount(0, 1));
		final String report = jack.accountsReport();
		assertEquals("Accounts of client: jack\n" + jacksSavingsAccountNo + "\tisOwner\t 0,01\tJack's Savings\n"
				+ jacksGiroAccountNo + "\tisOwner\t999999999,99\tJack's Giro\n", report);
	}

	@Test
	public void transfer() {
		final Client jack = bankService.createClient("jack", LocalDate.parse("1966-12-31"));
		final Account jacksGiro = jack.createAccount("Jack's Giro").getAccount();
		final Account jacksSavings = jack.createAccount("Jack's Savings").getAccount();
		final Amount minimumBalance = Account.getMinimumBalance();
		final Amount maximumTransferAmount = minimumBalance.times(-1);
		final AccountNo jacksSavingsAccountNo = jacksSavings.accountNo();
		jack.transfer(jacksGiro, jacksSavingsAccountNo, maximumTransferAmount);
		final String report = jack.accountsReport();
		final String maximumTransferAmountString = maximumTransferAmount.toString();
		final AccountNo jacksGiroAccountNo = jacksGiro.accountNo();
		assertEquals("Accounts of client: jack\n" + jacksSavingsAccountNo + "\tisOwner\t" + maximumTransferAmountString + "\tJack's Savings\n"
				+ jacksGiroAccountNo + "\tisOwner\t-" + maximumTransferAmountString + "\tJack's Giro\n", report);
	}

	@Test
	public void transferExc() {
		final Client jack = bankService.createClient("jack", LocalDate.parse("1966-12-31"));
		final Account jacksGiro = jack.createAccount("Jack's Giro").getAccount();
		final Account jacksSavings = jack.createAccount("Jack's Savings").getAccount();
		try {
			jack.transfer(jacksGiro, jacksSavings.accountNo(), new Amount(0));
			fail("Client.AmountExc expected");
		} catch (Client.AmountExc expected) {
		}

		final Client chloe = bankService.createClient("chloe", LocalDate.parse("1992-12-01"));
		try {
			chloe.transfer(jacksGiro, jacksSavings.accountNo(), new Amount(0, 01));
			fail("Client.WithoutRightExc expected");
		} catch (Client.WithoutRightExc expected) {
		}

		final Amount minimumBalance = Account.getMinimumBalance();
		final Amount maximumTransferAmount = minimumBalance.times(-1);
		final Amount tooGreatAmount = maximumTransferAmount.plus(new Amount(0, 01));
		try {
			jack.transfer(jacksGiro, jacksSavings.accountNo(), tooGreatAmount);
			fail("Client.MinimumBalanceExc expected");
		} catch (Client.MinimumBalanceExc expected) {
		}
	}

	@Test
	public void addAccountManager() {
		// GIVEN:
		final Client jack = bankService.createClient("jack", LocalDate.parse("1966-12-31"));
		final Account jacksGiro = jack.createAccount("Jack's Giro").getAccount();
		final Account jacksSavings = jack.createAccount("Jack's Savings").getAccount();
		final Client chloe = bankService.createClient("chloe", LocalDate.parse("1992-12-01"));

		// Client cannot transfer from an account, if she is not manager of the account:
		try {
			chloe.transfer(jacksGiro, jacksSavings.accountNo(), new Amount(0, 01));
			fail("Client.WithoutRightExc expected");
		} catch (Client.WithoutRightExc expected) {
		}

		// Client cannot add an account manager, if she is not owner of the account:
		try {
			chloe.addAccountManager(jacksGiro, chloe);
			fail("Client.NotOwnerExc expected");
		} catch (Client.NotOwnerExc expected) {
		}

		// WHEN:
		jack.addAccountManager(jacksGiro, chloe);

		// THEN:
		// Now chloe can transfer from jacksGiro account:
		chloe.transfer(jacksGiro, jacksSavings.accountNo(), new Amount(0, 01));

		// But chloe is only manager of the account, not owner. She cannot add another
		// Manager to the account.
		final Client tony = bankService.createClient("tony", LocalDate.parse("1964-03-22"));
		try {
			chloe.addAccountManager(jacksGiro, tony);
			fail("Client.NotOwnerExc expected");
		} catch (Client.NotOwnerExc expected) {
		}

		// No client can be added twice as a manager to the same account:
		try {
			jack.addAccountManager(jacksGiro, chloe);
			fail("Client.DoubleManagerExc expected");
		} catch (Client.DoubleManagerExc expected) {
		}
	}

	// TODO Test against double adding of a Client as manager to the same Account.
	// 17-08-18

	/**
	 * Makes a String representation of all passed clients, separated by commas.
	 * 
	 * @param clients
	 *            a collection of {@link Client}s
	 * @return the String representation in the format "1999-12-31 name1, 2017-12-31
	 *         name2"
	 */
	private String stringize(Iterable<Client> clients) {
		final StringBuilder result = new StringBuilder();
		for (final Client client : clients) {
			if (result.length() > 0) {
				result.append(", ");
			}
			assertNotNull(client.getId());
			result.append(client.getBirthDate());
			result.append(' ');
			result.append(client.getUsername());
		}
		return result.toString();
	}

}
