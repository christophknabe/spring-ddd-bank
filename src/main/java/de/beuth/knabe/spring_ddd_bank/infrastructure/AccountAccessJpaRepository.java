package de.beuth.knabe.spring_ddd_bank.infrastructure;

import de.beuth.knabe.spring_ddd_bank.domain.Account;
import de.beuth.knabe.spring_ddd_bank.domain.AccountAccess;
import de.beuth.knabe.spring_ddd_bank.domain.Amount;
import de.beuth.knabe.spring_ddd_bank.domain.Client;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountAccessRepository;
import de.beuth.knabe.spring_ddd_bank.infrastructure.imports.ImportedAccountAccessJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**A Repository for {@link de.beuth.knabe.spring_ddd_bank.domain.AccountAccess} link objects implemented with Spring Data JPA.
 * @author Christoph Knabe
 * @since 2017-03-06
 */
@Service
public class AccountAccessJpaRepository implements AccountAccessRepository {

    private final ImportedAccountAccessJpaRepository impl;

    @Autowired
    public AccountAccessJpaRepository(final ImportedAccountAccessJpaRepository impl) {
        this.impl = impl;
    }

    public void deleteAll(){impl.deleteAll();}

    public AccountAccess save(final AccountAccess accountAccess){
        return impl.save(accountAccess);
    }

    @Override
    public void delete(AccountAccess accountAccess) {
        impl.delete(accountAccess);
    }

    @Override
    public List<AccountAccess> findManagedAccountsOf(Client client, boolean asOwner) {
        return impl.findAllByClientAndIsOwnerGreaterThanEqualOrderByIdDesc(client, asOwner);
    }

    @Override
    public List<AccountAccess> findFullAccounts(final Amount minBalance) {
        return impl.findAllByAccountBalanceCentsGreaterThanEqualOrderByAccountBalanceCentsDescClientIdDesc(minBalance.getCents());
    }

    @Override
    public Optional<AccountAccess> find(final Client client, final Account account) {
        return impl.findOneByClientAndAccount(client, account);
    }

}
