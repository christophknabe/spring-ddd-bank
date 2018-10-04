package de.beuth.knabe.spring_ddd_bank.domain;

import de.beuth.knabe.spring_ddd_bank.domain.base.EntityBase;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountAccessRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountRepository;
import multex.Exc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import javax.persistence.Column;
import javax.persistence.Entity;

import static multex.MultexUtil.create;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * A client of a bank along with some methods he can do. This entity is a Rich
 * Domain Object. It can access services injected into it by Spring DODI.
 */
@Entity
@Configurable
public class Client extends EntityBase<Client> {

	private String username;
	private LocalDate birthDate;

	/** Necessary for JPA entities internally. */
	@SuppressWarnings("unused")
	private Client() {
	}

	/**
	 * Creates a Client from its user name and birthDate without saving it. For
	 * simplicity we do not store a full name of the Client.
	 * 
	 * @param username
	 *            the unique user name for the new {@link Client}
	 * @param birthDate
	 *            the birth date of the new {@link Client}
	 */
	public Client(final String username, final LocalDate birthDate) {
		this.username = username;
		this.birthDate = birthDate;
	}

	/**
	 * Returns the username of this client, by which he is supposed to login into
	 * the application.
	 * 
	 * @return the unique username of this {@link Client}
	 */
	@Column(unique = true, nullable = false)
	public String getUsername() {
		return username;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	@Override
	public String toString() {
		return String.format("Client{id=%d, name='%s', birthDate='%s'}", getId(), username, birthDate);
	}

	// Required repositories as by Ports and Adapters Pattern:

	@Autowired
	private transient AccountAccessRepository accountAccessRepository;
	@Autowired
	private transient AccountRepository accountRepository;

	/**
	 * Command: Creates a bank account with the given accountName and a zero
	 * balance.
	 * 
	 * @param accountName
	 *            a mnemonic for the purpose or usage of this account
	 * @return the link object for accessing the owner and the created account
	 */
	public AccountAccess createAccount(final String accountName) {
		final Account account = accountRepository.save(new Account(accountName));
		final AccountAccess accountAccess = new AccountAccess(this, true, account);
		return accountAccessRepository.save(accountAccess);
	}

	/**
	 * Command: Deposits the given amount into the destination account.
	 * 
	 * @param destination
	 *            the {@link Account} where the given {@link Amount} will be
	 *            deposited
	 * @param amount
	 *            the {@link Amount} which will be deposited
	 * 
	 * @throws AmountExc
	 *             Illegal amount (negative or zero)
	 */
	public void deposit(final Account destination, final Amount amount) throws AmountExc {
		// 1. Error checking:
		if (amount.compareTo(Amount.ZERO) <= 0) {
			throw create(Client.AmountExc.class, amount);
		}
		try {
			// 2. Do modifications:
			destination.setBalance(destination.getBalance().plus(amount));
			accountRepository.save(destination);
		} catch (final Exception ex) {
			throw new multex.Failure("Amount of {0} EUR could not be deposited.", ex, amount);
		}
	}

	/**
	 * Command: Transfers the given amount from the source account to the
	 * destination account.
	 * 
	 * @param source
	 *            the {@link Account} from which the {@link Amount} will be taken
	 * @param destination
	 *            the {@link Account} to which the {@link Amount} will be transfered
	 * @param amount
	 *            the {@link Amount} to be transfered
	 * 
	 * @throws WithoutRightExc
	 *             The sender is not a manager of the source account.
	 * @throws AmountExc
	 *             Illegal amount (negative or zero)
	 * @throws MinimumBalanceExc
	 *             The source account's balance would fall under the minimum
	 *             balance.
	 */
	public void transfer(final Account source, final Account destination, final Amount amount)
			throws AmountExc, WithoutRightExc, MinimumBalanceExc {
		// 1. Error checking:
		if (amount.toDouble() <= 0) {
			throw create(Client.AmountExc.class, amount);
		}
		final Optional<AccountAccess> accountAccessOptional = accountAccessRepository.find(this, source);
		if (!accountAccessOptional.isPresent()) {
			throw create(WithoutRightExc.class, getId(), source.getId());
		}
		final Amount newBalance = source.getBalance().minus(amount);
		if (newBalance.compareTo(Account.getMinimumBalance()) < 0) {
			throw create(MinimumBalanceExc.class, newBalance, Account.getMinimumBalance());
		}

		// 2. Do modifications:
		source.setBalance(source.getBalance().minus(amount));
		destination.setBalance(destination.getBalance().plus(amount));
		accountRepository.save(source);
		accountRepository.save(destination);
	}

	/**
	 * Client with ID {0} ist neither owner nor manager of the account with ID {1}.
	 */
	@SuppressWarnings("serial")
	public static class WithoutRightExc extends multex.Exc {
	}

	/** New balance {0} EUR would become lower than minimum balance {1} EUR. */
	@SuppressWarnings("serial")
	public static class MinimumBalanceExc extends multex.Exc {
	}

	/**
	 * Command: Adds the given manager Client to the given account in the role as
	 * manager, but not owner.
	 * 
	 * @param account
	 *            the {@link Account} to be managed
	 * @param manager
	 *            the {@link Client} to e given manager rights for the
	 *            {@link Account}
	 * @return the {@link AccountAccess} object created and saved
	 * 
	 * @throws NotOwnerExc
	 *             this Client is not owner of the account.
	 * @throws DoubleManagerExc
	 *             the given manager Client is already manager of the account.
	 */
	public AccountAccess addAccountManager(final Account account, Client manager) {
		final Optional<AccountAccess> ownerAccessOptional = accountAccessRepository.find(this, account);
		if (!ownerAccessOptional.isPresent()) {
			throw create(NotOwnerExc.class, this.getId(), account.getId());
		}
		final AccountAccess ownerAccess = ownerAccessOptional.get();
		if (!ownerAccess.isOwner()) {
			throw create(NotOwnerExc.class, this.getId(), account.getId());
		}
		final Optional<AccountAccess> managerAccessOptional = accountAccessRepository.find(manager, account);
		if (managerAccessOptional.isPresent()) {
			throw create(DoubleManagerExc.class, manager.getId(), account.getId());
		}
		final AccountAccess managerAccountAccess = new AccountAccess(manager, false, account);
		return accountAccessRepository.save(managerAccountAccess);
	}

	/** Client with ID {0} is not owner of the account with ID {1}. */
	@SuppressWarnings("serial")
	public static class NotOwnerExc extends multex.Exc {
	}

	/** Client with ID {0} is already manager of the account with ID {1}. */
	@SuppressWarnings("serial")
	public static class DoubleManagerExc extends multex.Exc {
	}

	/** Transfer amount {0} EUR illegal. Must be greater than 0! */
	@SuppressWarnings("serial")
	public static class AmountExc extends multex.Exc {
	}

	/**
	 * Query: Finds the {@link Account} with the given id.
	 * 
	 * @param id
	 *            the unique key of the account
	 * @return the found {@link Account}
	 * 
	 * @throws AccountNotFoundExc
	 *             There is no account object with the given id.
	 */
	public Account findAccount(final Long id) throws AccountNotFoundExc {
		final Optional<Account> optional = accountRepository.find(id);
		if (!optional.isPresent()) {
			throw create(AccountNotFoundExc.class, id);
		}
		return optional.get();
	}

	/** There is no Account object for the ID {0}. */
	@SuppressWarnings("serial")
	public static class AccountNotFoundExc extends Exc {
	}

	/**
	 * Query: Returns a report about all accounts this {@link Client} has the right
	 * to manage.
	 * 
	 * @return Report with a line for each account manageable. It has 3 columns: the
	 *         access right (isOwner|manages), the balance, the name of the account.
	 *         The columns are separated by tab characters.
	 */
	public String accountsReport() {
		final StringBuilder result = new StringBuilder();
		final List<AccountAccess> accountAccesses = accountAccessRepository.findManagedAccountsOf(this, false);
		result.append(String.format("Accounts of client: %s\n", getUsername()));
		for (final AccountAccess accountAccess : accountAccesses) {
			final String accessRight = accountAccess.isOwner() ? "isOwner" : "manages";
			final Account account = accountAccess.getAccount();
			result.append(
					String.format("%s\t%5.2f\t%s\n", accessRight, account.getBalance().toDouble(), account.getName()));
		}
		return result.toString();
	}

}
