package de.beuth.knabe.spring_ddd_bank.domain;

import de.beuth.knabe.spring_ddd_bank.domain.base.EntityBase;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import javax.persistence.Entity;
import static multex.MultexUtil.create;

import java.time.LocalDate;
import java.util.Optional;

@Entity 
@Configurable
public class Client extends EntityBase<Client> {

    private String name;
    private LocalDate birthDate;
    
    @Autowired
    private transient AccountAccessRepository accountAccessRepository;


    /**Necessary for JPA entities internally.*/
    private Client() {}

    public Client(final String name, final LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }

    /**Adds the given manager client to the given account in the role as manager, but not owner.
     * @throws NotOwnerExc this Client is not owner of the account.*/
    public AccountAccess addAccountManager(final Account account, Client manager) {
        final Optional<AccountAccess> accountAccessOptional = accountAccessRepository.find(this, account);
        final AccountAccess managerAccountAccess = new AccountAccess(manager, false, account);
        if(!accountAccessOptional.orElse(managerAccountAccess).isOwner()){
            throw create(NotOwnerExc.class, this.getId(), account.getId());
        }
        return accountAccessRepository.save(managerAccountAccess);
    }

    /**Client with ID {0} is not owner of the account with ID {1}.*/
    public static class NotOwnerExc extends multex.Exc {}

    @Override
    public String toString() {
        return String.format(
                "Client{id=%d, name='%s', birthDate='%s'}",
                getId(), name, birthDate);
    }

	public String getName() {
		return name;
	}

    public LocalDate getBirthDate() {
        return birthDate;
    }
}

