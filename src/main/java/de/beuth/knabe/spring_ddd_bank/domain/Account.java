package de.beuth.knabe.spring_ddd_bank.domain;

import de.beuth.knabe.spring_ddd_bank.domain.base.EntityBase;
import multex.Exc;
import static multex.MultexUtil.create;

import javax.persistence.Entity;

/**
 * An account, which a client of a bank can manage. This entity is an Anemic
 * Domain Object, as it only has stupid setters.
 */
@Entity
public class Account extends EntityBase<Account> {

	private String name;
	private Amount balance = new Amount(0);

	/** Necessary for JPA entities internally. */
	@SuppressWarnings("unused")
	private Account() {
	}

	public Account(final String name) {
		this.name = name;
	}
	
	public AccountNo accountNo() {
		final Long id = getId();
		if(id==null) {
			throw create(NotYetSavedExc.class);
		}
		return new AccountNo(id);
	}
	/** This account does not yet have an account number as it has never been saved. */
	@SuppressWarnings("serial")
	public static class NotYetSavedExc extends Exc {
	}

	@Override
	public String toString() {
		final Long id = getId();
		final String accountNo = id==null ? "" : Long.toString(id);
		return String.format("Account{accountNo=%s, name='%s', balance='%s'}", accountNo, name, balance);
	}

	public String getName() {
		return name;
	}

	public Amount getBalance() {
		return balance;
	}

	/**Sets the balance unchecked to the new amount. 
	 * Note: This method has only domain package visibility, so that it cannot be called from outside of the domain layer.
	 * @param amount the new amount */
	void setBalance(final Amount amount) {
		this.balance = amount;
	}

	/**
	 * Returns the minimum balance for the application.
	 * 
	 * @return The minimum balance, which must stay on each account.
	 */
	public static final Amount getMinimumBalance() {
		return new Amount(-1000, 0);
	}

}
