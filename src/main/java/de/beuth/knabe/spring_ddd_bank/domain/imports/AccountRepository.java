package de.beuth.knabe.spring_ddd_bank.domain.imports;

import java.util.Optional;

import de.beuth.knabe.spring_ddd_bank.domain.Account;
import de.beuth.knabe.spring_ddd_bank.domain.AccountNo;

/**
 * Required repository for {@link Account} objects.
 * 
 * @author Christoph Knabe
 * @since 2017-03-03
 */
public interface AccountRepository {

	/**
	 * Searches the {@link Account} object with the given account number.
	 * 
	 * @param acccountNo
	 *            unique account number of the searched account
	 * @return the {@link Account} object with the given account number, if existing.
	 * @throws IllegalArgumentException
	 *             acccountNo is null or empty
	 */
	Optional<Account> find(AccountNo acccountNo);

	/**
	 * Deletes all Accounts. Useful for test scenarios in order to start with an
	 * empty account set.
	 */
	void deleteAll();

	/**
	 * Saves the account giving it a unique, higher account number (accountNo).
	 * 
	 * @param account
	 *            the {@link Account} to be saved
	 * @return the modified instance
	 */
	Account save(Account account);

}
