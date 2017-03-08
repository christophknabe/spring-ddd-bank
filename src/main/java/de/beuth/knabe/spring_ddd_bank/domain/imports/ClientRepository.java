package de.beuth.knabe.spring_ddd_bank.domain.imports;

import de.beuth.knabe.spring_ddd_bank.domain.Client;

import java.time.LocalDate;
import java.util.List;

/**Required repository for {@link Client} objects.
 * @author Christoph Knabe
 * @since 2017-03-01
 */
public interface ClientRepository {

    /**Deletes all Clients. Useful for test scenarions in order to start with an empty client set.*/
    void deleteAll();

    /**Gives the client a unique, higher ID and saves the client.
     * @return the modified instance*/
    Client save(Client client);

    /**Deletes the given client.*/
    void delete(Client client);

    /**Finds all {@link Client}s and returns them ordered by descending IDs.*/
    List<Client> findAll();

    /**Finds all {@link Client}s born at the given date or later, and returns them ordered firstly by their descending birth date, and secondly by descending IDs.*/
    List<Client> findAllBornFrom(LocalDate minDate);

}
