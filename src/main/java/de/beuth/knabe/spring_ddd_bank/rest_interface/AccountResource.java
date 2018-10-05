package de.beuth.knabe.spring_ddd_bank.rest_interface;

import de.beuth.knabe.spring_ddd_bank.domain.Account;

/** Data about an Account of a bank. Usable as Data Transfer Object. */
public class AccountResource {

	/** Unique Number of the Account. */
	public Long accountNo;

	/** Distinguishing name of the Account for the owning Client. */
	public String name;

	/** The balance of the Account in euros. */
	public double balance;

	/** Necessary for Jackson */
	public AccountResource() {
	}

	/**
	 * Constructs an AccountResource with the data of the passed Account entity.
	 * 
	 * @param entity
	 *            the entity to be converted
	 */
	public AccountResource(final Account entity) {
		this.accountNo = entity.accountNo().toLong();
		this.name = entity.getName();
		this.balance = entity.getBalance().toDouble();
	}

	@Override
	public String toString() {
		return String.format("Account{accountNo=%d, name='%s', balance='%s'}", accountNo, name, balance);
	}

}
