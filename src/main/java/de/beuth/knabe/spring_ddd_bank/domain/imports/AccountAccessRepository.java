package de.beuth.knabe.spring_ddd_bank.domain.imports;

import de.beuth.knabe.spring_ddd_bank.domain.Account;
import de.beuth.knabe.spring_ddd_bank.domain.AccountAccess;
import de.beuth.knabe.spring_ddd_bank.domain.Amount;
import de.beuth.knabe.spring_ddd_bank.domain.Client;

import java.util.List;
import java.util.Optional;

/**
 * Required repository for
 * {@link de.beuth.knabe.spring_ddd_bank.domain.AccountAccess} objects.
 * 
 * @author Christoph Knabe
 * @since 2017-03-08
 */
public interface AccountAccessRepository {

	/**
	 * Deletes all {@link AccountAccess} objects. Linked {@link Client}s or
	 * {@link Account}s must be deleted before.
	 */
	void deleteAll();

	/**
	 * Saves the passed object. Linked {@link Client} and {@link Account} must be
	 * saved before.
	 * 
	 * @param accountAccess
	 *            the {@link AccountAccess} object to be saved
	 * @return the saved instance
	 */
	AccountAccess save(AccountAccess accountAccess);

	/**
	 * Deletes the given {@link AccountAccess} object.
	 * 
	 * @param accountAccess
	 *            the AccountAccess object to be deleted
	 */
	void delete(AccountAccess accountAccess);

	/**
	 * Finds all {@link AccountAccess} objects, which the given client may manage.
	 * 
	 * @param client
	 *            the Client, whose managed accounts are searched.
	 * @param asOwner
	 *            if true returns only {@link AccountAccess} objects, where the
	 *            {@link Client} is owner.
	 * @return accesss objects to all accounts managed by the given {@link Client}
	 */
	List<AccountAccess> findManagedAccountsOf(Client client, boolean asOwner);

	/**
	 * Finds accounts with a minimum balance.
	 * 
	 * @param minBalance
	 *            accounts with a balance equal or greater than the given minBalance
	 *            will be included.
	 * @return access objects to the full accounts, ordered by the descending
	 *         balance, secondly by descending ids of their managing clients.
	 */
	List<AccountAccess> findFullAccounts(final Amount minBalance);

	/**
	 * Finds the access rights of the client for the account.
	 * 
	 * @param client
	 *            a Client of the bank
	 * @param account
	 *            an account, which the clients want to use
	 * @return {@link AccountAccess} object for the given client and account, if
	 *         existing.
	 */
	Optional<AccountAccess> find(Client client, Account account);

}
