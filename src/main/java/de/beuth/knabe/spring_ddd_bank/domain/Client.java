package de.beuth.knabe.spring_ddd_bank.domain;

import de.beuth.knabe.spring_ddd_bank.domain.Client.AmountExc;
import de.beuth.knabe.spring_ddd_bank.domain.base.EntityBase;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountAccessRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountRepository;
import multex.Exc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import javax.persistence.Entity;
import static multex.MultexUtil.create;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Entity 
@Configurable
public class Client extends EntityBase<Client> {

    private String name;
    private LocalDate birthDate;


    /**Necessary for JPA entities internally.*/
    @SuppressWarnings("unused")
	private Client() {}

    public Client(final String name, final LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }

	public String getName() {
		return name;
	}

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public String toString() {
        return String.format(
                "Client{id=%d, name='%s', birthDate='%s'}",
                getId(), name, birthDate);
    }
    
    @Autowired
    private transient AccountAccessRepository accountAccessRepository;
    
    @Autowired
    private transient AccountRepository accountRepository; 

	/**Creates a bank account with the given accountName and a zero balance.
	 * @param accountName a mnemonic for the purpose or usage of this account
	 * @return the link object for accessing the owner and the created account
	 */
	public AccountAccess createAccount(final String accountName){
	    final Account account = accountRepository.save(new Account(accountName));
	    final AccountAccess accountAccess = new AccountAccess(this, true, account);
	    return accountAccessRepository.save(accountAccess);
	}

    /**Deposits the given amount into the destination account.
     * @throws Client.AmountExc Illegal amount (negative or zero)
     */
    public void deposit(
            final Account destination,
            final Amount amount
    ) throws Client.AmountExc
    {
        //1. Error checking:
        if(amount.compareTo(Amount.ZERO) <= 0){
            throw create(Client.AmountExc.class, amount);
        }
        try {
            //2. Do modifications:
            destination.setBalance(destination.getBalance().plus(amount));
            accountRepository.save(destination);
        }catch(final Exception ex){
            throw new multex.Failure("Amount of {0} EUR could not be deposited.", ex, amount);
        }
    }

	/**Transfers the given amount from the source account to the destination account.
	 * @throws WithoutRightExc The sender is not a manager of the source account.
	 * @throws Client.AmountExc Illegal amount (negative or zero)
	 * @throws MinimumBalanceExc The source account's balance would fall under the minimum balance.
	 */
	public void transfer(
			final Account source, final Account destination, final Amount amount
	) throws Client.AmountExc, WithoutRightExc, MinimumBalanceExc
	{
	    //1. Error checking:
	    if(amount.toDouble() <= 0){
	        throw create(Client.AmountExc.class, amount);
	    }
	    final Optional<AccountAccess> accountAccessOptional = accountAccessRepository.find(this, source);
	    if(!accountAccessOptional.isPresent()){
	        throw create(WithoutRightExc.class, getId(), source.getId());
	    }
	    final Amount newBalance = source.getBalance().minus(amount);
	    if(newBalance.compareTo(Account.getMinimumBalance()) < 0){
	        throw create(MinimumBalanceExc.class, newBalance, Account.getMinimumBalance());
	    }
	
	    //2. Do modifications:
	    source.setBalance(source.getBalance().minus(amount));
	    destination.setBalance(destination.getBalance().plus(amount));
	    accountRepository.save(source);
	    accountRepository.save(destination);
	}

	/**Client with ID {0} ist neither owner nor manager of the account with ID {1}.*/
    @SuppressWarnings("serial")
	public static class WithoutRightExc extends multex.Exc {}

    /**New balance {0} EUR would become lower than minimum balance {1} EUR.*/
    @SuppressWarnings("serial")
	public static class MinimumBalanceExc extends multex.Exc {}


    /**Adds the given manager Client to the given account in the role as manager, but not owner.
     * @throws NotOwnerExc this Client is not owner of the account.
     * @throws DoubleManagerExc the given manager Client is already manager of the account.*/
    public AccountAccess addAccountManager(final Account account, Client manager) {
        final Optional<AccountAccess> ownerAccessOptional = accountAccessRepository.find(this, account);
        if(!ownerAccessOptional.isPresent()){
            throw create(NotOwnerExc.class, this.getId(), account.getId());
        }
        final AccountAccess ownerAccess = ownerAccessOptional.get();
        if(!ownerAccess.isOwner()){
            throw create(NotOwnerExc.class, this.getId(), account.getId());
        }
        final Optional<AccountAccess> managerAccessOptional = accountAccessRepository.find(manager, account);
        if(managerAccessOptional.isPresent()){
            throw create(DoubleManagerExc.class, manager.getId(), account.getId());
        }
        final AccountAccess managerAccountAccess = new AccountAccess(manager, false, account);
        return accountAccessRepository.save(managerAccountAccess);
    }

    /**Client with ID {0} is not owner of the account with ID {1}.*/
    @SuppressWarnings("serial")
	public static class NotOwnerExc extends multex.Exc {}
    
    /**Client with ID {0} is already manager of the account with ID {1}.*/
    @SuppressWarnings("serial")
	public static class DoubleManagerExc extends multex.Exc {}

	/**Transfer amount {0} EUR illegal. Must be greater than 0!*/
	@SuppressWarnings("serial")
	public static class AmountExc extends multex.Exc {}


	/**Returns a report about all accounts the passed {@link Client} has access to. */
	public String getAccountsReport() {
	    final StringBuilder result = new StringBuilder();
	    final List<AccountAccess> accountAccesses = accountAccessRepository.findManagedAccountsOf(this, false);
	    result.append(String.format("Accounts of client: %s\n", getName()));
	    for(final AccountAccess accountAccess: accountAccesses){
	        final String accessRight = accountAccess.isOwner() ? "isOwner " : "manages";
	        final Account account = accountAccess.getAccount();
	        result.append(String.format("%s\t%5.2f\t%s\n", accessRight, account.getBalance().toDouble(), account.getName()));
	    }
	    return result.toString();
	}
    
}

