package de.beuth.knabe.spring_ddd_bank.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountAccessRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.ClientRepository;

/**This is a domain test scope service for cleaning the database.
 * @author Christoph Knabe
 * @since 2017-03-08
 */
@Service
class CleanupService {

    private final ClientRepository clientRepository;

    private final AccountRepository accountRepository;

    private final AccountAccessRepository accountAccessRepository;

    /**Constructs the cleanup service using the passed required repositories as by Ports and Adapters Pattern.*/
    @Autowired
    public CleanupService(final ClientRepository clientRepository, final AccountRepository accountRepository, final AccountAccessRepository accountAccessRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.accountAccessRepository = accountAccessRepository;
    }

    /**Deletes all entities from all used repositories.*/
    void deleteAll(){
        accountAccessRepository.deleteAll();
        accountRepository.deleteAll();
        clientRepository.deleteAll();
    }

}
