package de.beuth.knabe.spring_ddd_bank.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountAccessRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.ClientRepository;
import multex.MultexUtil;

/**This is a domain service for a clerk of a bank.
 * @author Christoph Knabe
 * @since 2017-03-01
 */
@Service
public class BankService {

    //Required repositories as by Ports and Adapters Pattern:
    private final ClientRepository clientRepository;
    private final AccountAccessRepository accountAccessRepository;


    @Autowired
    public BankService(final ClientRepository clientRepository, final AccountAccessRepository accountAccessRepository) {
        this.clientRepository = clientRepository;
        this.accountAccessRepository = accountAccessRepository;
    }

    /**Command: Creates a bank client with the given name and birth date.
     * @param name the full name of the new client, must not be empty
     * @param birthDate the birth date of the new client, must not be null
     */
    public Client createClient(final String name, final LocalDate birthDate) {
        final Client client = clientRepository.save(new Client(name, birthDate));
        return client;
    }

    /**Command: Deletes the given {@link Client}.
     * @throws DeleteExc Client has accounts, where he is the owner.*/
    public void deleteClient(final Client client){
        final List<AccountAccess> managedAccounts = accountAccessRepository.findManagedAccountsOf(client, true);
        for(final AccountAccess accountAccess: managedAccounts){
            if(accountAccess.isOwner()){
                throw MultexUtil.create(DeleteExc.class, client, accountAccess.getAccount());
            }else{
                accountAccessRepository.delete(accountAccess);
            }
        }
        clientRepository.delete(client);
    }

    /**Cannot delete client {0}, Still owns account {1}.*/
    public static class DeleteExc extends multex.Exc {}
    
    /**Query: Finds the client with the given id, if exists.*/
    public Optional<Client> findClient(final Long id) {
        return clientRepository.find(id);
    }

    /**Query: Finds all clients of the bank. They are ordered by their descending IDs, that means the newest come first.*/
    public List<Client> findAllClients(){
        return clientRepository.findAll();
    }

    /**Query: Finds all clients of the bank, who are born at the given date or later. They are ordered by their ascending age and secondly by their descending IDs.*/
    public List<Client> findYoungClients(final LocalDate fromBirth){
        return clientRepository.findAllBornFrom(fromBirth);
    }

    /**Query: Finds all clients of the bank, who own or manage an account with the given mimimum balance. They are ordered by their descending account balance and secondly by their descending IDs.*/
    public List<Client> findRichClients(final Amount minBalance){
        final List<AccountAccess> fullAccounts = accountAccessRepository.findFullAccounts(minBalance);
        final Stream<Client> richClients = fullAccounts.stream().map(accountAccess -> accountAccess.getClient()).distinct();
        return richClients.collect(Collectors.toList());
    }

}
