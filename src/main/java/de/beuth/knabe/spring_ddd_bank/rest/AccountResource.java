package de.beuth.knabe.spring_ddd_bank.rest;

import de.beuth.knabe.spring_ddd_bank.domain.Account;

/**Data about an Account of a bank. Usable as Data Transfer Object.*/
public class AccountResource {

    /**Unique ID of the Account.*/
	public Long id;

    /**Distinguishing name of the Account for the owning Client.*/
    public String name;

	/**The balance of the Account in euros.*/
    public double balance;


    /**Necessary for Jackson*/
	public AccountResource() {}

    /**Constructs an AccountResource with the data of the passed Account entity.*/
    public AccountResource(final Account entity) {
    	this.id = entity.getId();
        this.name = entity.getName();
        this.balance = entity.getBalance().toDouble();
    }

    @Override
    public String toString() {
        return String.format(
                "Account{id=%d, name='%s', balance='%s'}",
                id, name, balance);
    }
    
}

