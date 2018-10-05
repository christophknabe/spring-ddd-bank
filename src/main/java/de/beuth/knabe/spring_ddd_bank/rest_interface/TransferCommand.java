package de.beuth.knabe.spring_ddd_bank.rest_interface;

/**Command to transfer the amount in euros from the Account with the given sourceAccountNo to the Account with the given destinationAccountNo.*/
public class TransferCommand {

	public Long sourceAccountNo;
	public Long destinationAccountNo;
	public double amount;

    @Override
    public String toString() {
        return String.format(
                "TransferCommand{sourceAccountNo=%d, destinationAccountNo=%d, amount=%s}",
                sourceAccountNo, destinationAccountNo, amount);
    }

}
