package de.beuth.knabe.spring_ddd_bank.domain.imports;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import de.beuth.knabe.spring_ddd_bank.domain.Client;

/**
 * Required repository for {@link Client} objects.
 * 
 * @author Christoph Knabe
 * @version 2018-10-03
 * @since 2017-03-01
 */
public interface ClientRepository {

	/**
	 * Deletes all Clients. Useful for test scenarios in order to start with an
	 * empty {@link Client} set.
	 */
	void deleteAll();

	/**Saves a client giving it a unique, higher ID
	 * @param client the {@link Client} to be saved
	 * 
	 * @return the modified instance
	 */
	Client save(Client client);

	/** Deletes the given client. 
	 * @param client the {@link Client} to be deleted*/
	void delete(Client client);

	/**Searches the client object by its ID.
	 * @param id the unique ID of the searched client object
	 * @return the {@link Client} object with the given id, if existing.
	 * 
	 * 
	 * @throws IllegalArgumentException
	 *             id is null
	 */
	Optional<Client> find(Long id);

	/**Searches the client object by username.
	 * @param username the unique username of the searched client
	 * @return  the {@link Client} object with the given username, if existing.
	 * @throws IllegalArgumentException
	 *             username is null or empty
	 */
	Optional<Client> find(String username);

	/** Finds all {@link Client}s. 
	 * @return all clients ordered by descending IDs*/
	List<Client> findAll();

	/**
	 * Finds all {@link Client}s born at the given date or later.
	 * @param minDate the oldest birth date of clients
	 * @return the clients ordered firstly by their descending birth date, and secondly by descending
	 * IDs.
	 */
	List<Client> findAllBornFrom(LocalDate minDate);

}
