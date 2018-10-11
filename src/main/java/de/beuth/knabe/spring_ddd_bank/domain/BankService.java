package de.beuth.knabe.spring_ddd_bank.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountAccessRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.ClientRepository;
import multex.Exc;

import static multex.MultexUtil.create;

/**
 * This is a domain service for a clerk of a bank.
 * 
 * @author Christoph Knabe
 * @since 2017-03-01
 */
@Service
public class BankService {

	// Required repositories as by Ports and Adapters Pattern:
	//See http://www.dossier-andreas.net/software_architecture/ports_and_adapters.html
	private final ClientRepository clientRepository;
	private final AccountAccessRepository accountAccessRepository;

	@Autowired
	public BankService(final ClientRepository clientRepository, final AccountAccessRepository accountAccessRepository) {
		this.clientRepository = clientRepository;
		this.accountAccessRepository = accountAccessRepository;
	}

	/**
	 * Command: Creates a new bank {@link Client} with given username and birthDate
	 * and saves it giving it a unique ID.
	 * 
	 * @param username
	 *            the unique username of the new client. It must match the regular
	 *            expression <code>[a-z_A-Z][a-z_A-Z0-9]{0,30}</code>.
	 * @param birthDate
	 *            the birth date of the new client, must not be null
	 * @return the saved new {@link Client} with the ID set.
	 * @throws UsernameExc
	 *             the username does not match the required pattern.
	 * 
	 */
	public Client createClient(final String username, final LocalDate birthDate) {
		final Pattern pattern = Pattern.compile("[a-z_A-Z][a-z_A-Z0-9]{0,30}");
		if (!pattern.matcher(username).matches()) {
			throw create(UsernameExc.class, username);
		}
		final Client client = clientRepository.save(new Client(username, birthDate));
		return client;
	}

	/**
	 * Illegal username "{0}". Must have 1..31 characters, start with a letter and
	 * contain only english letters, underscores, and decimal digits.
	 */
	@SuppressWarnings("serial")
	public static class UsernameExc extends multex.Exc {
	}

	/**
	 * Command: Deletes the given {@link Client}. The {@link Client} looses all
	 * manager account accesses to accounts, where he was manager.
	 * 
	 * @param client
	 *            the {@link Client} to be deleted
	 * 
	 * @throws DeleteExc
	 *             Client has accounts, where he is the owner. So he cannot yet be
	 *             deleted.
	 */
	public void deleteClient(final Client client) {
		final List<AccountAccess> managedAccounts = accountAccessRepository.findManagedAccountsOf(client, true);
		for (final AccountAccess accountAccess : managedAccounts) {
			if (accountAccess.isOwner()) {
				throw create(DeleteExc.class, client, accountAccess.getAccount());
			} else {
				accountAccessRepository.delete(accountAccess);
			}
		}
		clientRepository.delete(client);
	}

	/** Cannot delete client {0}, Still owns account {1}. */
	@SuppressWarnings("serial")
	public static class DeleteExc extends multex.Exc {
	}

	/**
	 * Query: Finds the client with the given username.
	 * 
	 * @param username
	 *            the unique username to be used for locating the {@link Client}
	 * @return the {@link Client} with the given username
	 * 
	 * @throws ClientNotFoundExc
	 *             There is no client object with the given username.
	 */
	public Client findClient(final String username) throws ClientNotFoundExc {
		final Optional<Client> optional = clientRepository.find(username);
		if (!optional.isPresent()) {
			throw create(ClientNotFoundExc.class, username);
		}
		return optional.get();
	}

	/** There is no Client object for the username {0}. */
	@SuppressWarnings("serial")
	public static class ClientNotFoundExc extends Exc {
	}

	/**
	 * Query: Finds all clients of the bank.
	 * 
	 * @return all {@link Client}s of the bank ordered by their descending IDs, that
	 *         means the newest come first.
	 */
	public List<Client> findAllClients() {
		return clientRepository.findAll();
	}

	/**
	 * Query: Finds all clients of the bank, who are born at the given date or
	 * later.
	 * 
	 * @param fromBirth
	 *            the earliest birth date from which {@link Client}s are considered
	 * @return all young {@link Client}s ordered by their ascending age and secondly
	 *         by their descending IDs.
	 */
	public List<Client> findYoungClients(final LocalDate fromBirth) {
		return clientRepository.findAllBornFrom(fromBirth);
	}

	/**
	 * Query: Finds all clients of the bank, who own or manage an account with the
	 * given mimimum balance.
	 * 
	 * @return all {@link Client}s with {@link Account} with the given mimimum
	 *         balance. They are ordered by their descending account balance and
	 *         secondly by their descending IDs.
	 * @param minBalance
	 *            the minimum balance of considered {@link Account}s
	 */
	public List<Client> findRichClients(final Amount minBalance) {
		final List<AccountAccess> fullAccounts = accountAccessRepository.findFullAccounts(minBalance);
		final Stream<Client> richClients = fullAccounts.stream().map(accountAccess -> accountAccess.getClient())
				.distinct();
		return richClients.collect(Collectors.toList());
	}

}
