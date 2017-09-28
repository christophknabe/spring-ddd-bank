package de.beuth.knabe.spring_ddd_bank.rest_interface;

import de.beuth.knabe.spring_ddd_bank.domain.Account;
import de.beuth.knabe.spring_ddd_bank.domain.AccountAccess;

/**Data about an Account of a bank, and the access a Client of the bank has to it. Usable as Data Transfer Object.*/
public class AccountAccessResource {
	
	public Long clientId;
	public boolean isOwner;
	public Long accountId;
	public String accountName;
	public String accountBalance;
	
	@Deprecated
	public AccountAccessResource(
			final Long clientId, 
			final boolean isOwner, 
			final Long accountId, 
			final String accountName,
			final String accountBalance) 
	{
		this.clientId = clientId;
		this.isOwner = isOwner;
		this.accountId = accountId;
		this.accountName = accountName;
		this.accountBalance = accountBalance;
	}
	
	public AccountAccessResource(final AccountAccess entity){
    	final Account account = entity.getAccount();
		this.clientId = entity.getClient().getId();
		this.isOwner = entity.isOwner();
		this.accountId = account.getId();
		this.accountName = account.getName();
		this.accountBalance = Double.toString(account.getBalance().toDouble());
	}

}
