package de.beuth.knabe.spring_ddd_bank.domain;

import de.beuth.knabe.spring_ddd_bank.domain.base.EntityBase;

import javax.persistence.*;
import java.io.Serializable;

/**Many-to-many association "Client may access Account" with specific privileges.
 * @author  Christoph Knabe
 */
@Entity
public class AccountAccess extends EntityBase<AccountAccess> {

    @ManyToOne
    private Client client;

    private boolean isOwner;

    @ManyToOne
    private Account account;

    /**Necessary for JPA entities internally.*/
    private AccountAccess() {}

    public AccountAccess(final Client client, final boolean isOwner, final Account account) {
        this.client = client;
        this.isOwner = isOwner;
        this.account = account;
    }

    /**Returns the Client who is managing the Account.*/
    public Client getClient(){return client;}

    /**Returns true if the Client is the owner of the Account. In contrary he would be only manager of the account.*/
    public boolean isOwner() {
        return isOwner;
    }

    /**Returns the Account accessible by this object.*/
    public Account getAccount() {
        return account;
    }

    @Override
    public String toString() {
        return String.format(
                "%s{client='%s', isOwner='%b', account='%s'}",
                getClass().getSimpleName(), client, isOwner, account);
    }

}

