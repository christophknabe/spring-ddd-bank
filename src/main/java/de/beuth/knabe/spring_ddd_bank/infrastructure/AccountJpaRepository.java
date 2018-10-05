package de.beuth.knabe.spring_ddd_bank.infrastructure;

import de.beuth.knabe.spring_ddd_bank.domain.Account;
import de.beuth.knabe.spring_ddd_bank.domain.AccountNo;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountRepository;
import de.beuth.knabe.spring_ddd_bank.infrastructure.imports.ImportedAccountJpaRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**A Repository for Account entities implemented with Spring Data JPA.
 * @author Christoph Knabe
 * @since 2017-03-06
 */
@Service
public class AccountJpaRepository implements AccountRepository {

    private final ImportedAccountJpaRepository impl;

    @Autowired
    public AccountJpaRepository(final ImportedAccountJpaRepository impl) {
        this.impl = impl;
    }
    
	@Override
	public Optional<Account> find(AccountNo acccountNo) {
		return impl.findOneById(acccountNo.toLong());
	}

    public void deleteAll(){impl.deleteAll();}

    public Account save(final Account account){
        return impl.save(account);
    }

}
