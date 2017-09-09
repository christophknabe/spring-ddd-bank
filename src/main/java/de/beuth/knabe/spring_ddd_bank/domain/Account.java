package de.beuth.knabe.spring_ddd_bank.domain;

import de.beuth.knabe.spring_ddd_bank.domain.base.EntityBase;

import javax.persistence.Entity;

@Entity
public class Account extends EntityBase<Account> {

    private String name;
    private Amount balance = new Amount(0);

    /**Necessary for JPA entities internally.*/
    @SuppressWarnings("unused")
	private Account() {}

    public Account(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format(
                "Account{id=%d, name='%s', balance='%s'}",
                getId(), name, balance);
    }

	public String getName() {
		return name;
	}

    public Amount getBalance(){ return balance;}

    public void setBalance(final Amount amount){ this.balance = amount;}

	/**The minimum balance, which must stay on each account.*/
	public static final Amount getMinimumBalance(){return new Amount(-1000, 0);}

}

