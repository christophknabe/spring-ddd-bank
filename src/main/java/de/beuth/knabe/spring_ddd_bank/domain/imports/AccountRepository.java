package de.beuth.knabe.spring_ddd_bank.domain.imports;

import de.beuth.knabe.spring_ddd_bank.domain.Account;

/**Required repository for {@link Account} objects.
 * @author Christoph Knabe
 * @since 2017-03-03
 */
public interface AccountRepository {

    /**Deletes all Accounts. Useful for test scenarions in order to start with an empty account set.*/
    void deleteAll();

    /**Gives the account a unique, higher ID and saves the client.
     * @return the modified instance*/
    Account save(Account account);

}
