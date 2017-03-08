package de.beuth.knabe.spring_ddd_bank.domain;

import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountAccessRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static multex.MultexUtil.create;

/**This is a domain service for a client of a bank.
 * @author Christoph Knabe
 * @since 2017-03-03
 */
@Service
public class ClientService {

    private final ClientRepository clientRepository;

    private final AccountRepository accountRepository;

    private final AccountAccessRepository accountAccessRepository;

    /**Constructs the client service using the passed required repositories as by Ports and Adapters Pattern.*/
    @Autowired
    public ClientService(final ClientRepository clientRepository, final AccountRepository accountRepository, final AccountAccessRepository accountAccessRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.accountAccessRepository = accountAccessRepository;
    }

    /**Creates a bank account with the given accountName and a zero balance.
     * @param owner the client who will own the newly created account
     * @param accountName a mnemonic for the purpose or usage of this account
     * @return the link object for accessing the owner and the created account
     */
    public AccountAccess createAccount(final Client owner, final String accountName){
        final Account account = accountRepository.save(new Account(accountName));
        final AccountAccess accountAccess = new AccountAccess(owner, true, account);
        return accountAccessRepository.save(accountAccess);
    }

    /**Deposits the given amount into the destination account.
     * @throws AmountExc Illegal amount (negative or zero)
     */
    public void deposit(
            final Account destination,
            final Amount amount
    ) throws AmountExc
    {
        //1. Error checking:
        if(amount.compareTo(Amount.ZERO) <= 0){
            throw create(AmountExc.class, amount);
        }
        try {
            //2. Do modifications:
            destination.setBalance(destination.getBalance().plus(amount));
            accountRepository.save(destination);
        }catch(final Exception ex){
            throw new multex.Failure("Amount of {0} EUR could not be deposited.", ex, amount);
        }
    }


    /**Finds all clients of the bank. They are ordered by their ascending IDs, that means the same as by their creation time.*/
    public Iterable<Client> findAllClients(){
        return clientRepository.findAll();
    }

    /**Finds all clients of the bank, who are born at the given date or later. They are ordered by their birth date and secondly by their IDs, that means the same as by their creation time.*/
    public Iterable<Client> findYoungClients(final LocalDate fromBirth){
        return clientRepository.findAllBornFrom(fromBirth);
    }

    /**Returns a report about all accounts the passed {@link Client} has access to. */
    public String getAccountsReport(final Client managedBy) {
        final StringBuilder result = new StringBuilder();
        final List<AccountAccess> accountAccesses = accountAccessRepository.findManagedAccountsOf(managedBy, false);
        result.append(String.format("Accounts of client: %s\n", managedBy.getName()));
        for(final AccountAccess accountAccess: accountAccesses){
            final String accessRight = accountAccess.isOwner() ? "isOwner " : "manages";
            final Account account = accountAccess.getAccount();
            result.append(String.format("%s\t%5.2f\t%s\n", accessRight, account.getBalance().toDouble(), account.getName()));
        }
        return result.toString();
    }

    /**The minimum balance, which must stay on each account.*/
    public static final Amount getMinimumBalance(){return new Amount(-1000, 0);}

    /**Transfers the given amount from the source account to the destination account.
     * @throws WithoutRightExc The sender is not a manager of the source account.
     * @throws AmountExc Illegal amount (negative or zero)
     * @throws MinimumBalanceExc The source account's balance would fall under the minimum balance.
     */
    public void transfer(
            final Client sender,
            final Account source,
            final Account destination,
            final Amount amount
    ) throws AmountExc, WithoutRightExc, MinimumBalanceExc
    {
        //1. Error checking:
        if(amount.toDouble() <= 0){
            throw create(AmountExc.class, amount);
        }
        final Optional<AccountAccess> accountAccessOptional = accountAccessRepository.find(sender, source);
        if(!accountAccessOptional.isPresent()){
            throw create(WithoutRightExc.class, sender.getId(), source.getId());
        }
        final Amount newBalance = source.getBalance().minus(amount);
        if(newBalance.compareTo(getMinimumBalance()) < 0){
            throw create(MinimumBalanceExc.class, newBalance, getMinimumBalance());
        }

        //2. Do modifications:
        source.setBalance(source.getBalance().minus(amount));
        destination.setBalance(destination.getBalance().plus(amount));
        accountRepository.save(source);
        accountRepository.save(destination);
    }

    /**Client with ID {0} ist neither owner nor manager of the account with ID {1}.*/
    public static class WithoutRightExc extends multex.Exc {}

    /**Transfer amount {0} EUR illegal. Must be greater than 0!*/
    public static class AmountExc extends multex.Exc {}

    /**New balance {0} EUR would become lower than minimum balance {1} EUR.*/
    public static class MinimumBalanceExc extends multex.Exc {}


}
