package de.beuth.knabe.spring_ddd_bank.rest_interface;

/**Command to deposit the amount in euros to the Account with the given accountId.*/
public class DepositCommand {
	
	public Long accountId;
	public double amount;

    @Override
    public String toString() {
        return String.format(
                "DepositCommand{accountId=%d, amount='%s'}",
                accountId, amount);
    }

}
