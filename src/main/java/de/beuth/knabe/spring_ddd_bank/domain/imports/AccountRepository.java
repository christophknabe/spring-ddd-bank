package de.beuth.knabe.spring_ddd_bank.domain.imports;

import java.util.Optional;

import de.beuth.knabe.spring_ddd_bank.domain.Account;

/**
 * Required repository for {@link Account} objects.
 * 
 * @author Christoph Knabe
 * @since 2017-03-03
 */
public interface AccountRepository {

	/**
	 * Searches the {@link Account} object with the given id.
	 * 
	 * @param id
	 *            unique ID of the searched account
	 * @return the {@link Account} object with the given id, if existing.
	 * @throws IllegalArgumentException
	 *             id is null
	 */
	Optional<Account> find(Long id);

	/**
	 * Deletes all Accounts. Useful for test scenarios in order to start with an
	 * empty account set.
	 */
	void deleteAll();

	/**
	 * Saves the account giving it a unique, higher ID.
	 * 
	 * @param account
	 *            the {@link Account} to be saved
	 * @return the modified instance
	 */
	Account save(Account account);

}
