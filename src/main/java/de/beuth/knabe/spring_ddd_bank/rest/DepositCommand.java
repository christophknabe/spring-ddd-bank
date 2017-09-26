package de.beuth.knabe.spring_ddd_bank.rest;

/**Command to deposit the amount in euros to the Account with the given destinationAccountId.*/
public class DepositCommand {
	
	public Long destinationAccountId;
	public double amount;

    @Override
    public String toString() {
        return String.format(
                "DepositCommand{destinationAccountId=%d, amount='%s'}",
                destinationAccountId, amount);
    }

}
