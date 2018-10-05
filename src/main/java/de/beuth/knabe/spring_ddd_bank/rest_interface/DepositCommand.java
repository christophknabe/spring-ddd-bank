package de.beuth.knabe.spring_ddd_bank.rest_interface;

/**Command to deposit the amount in euros to the Account with the given account number.*/
public class DepositCommand {
	
	public Long accountNo;
	public double amount;

    @Override
    public String toString() {
        return String.format(
                "DepositCommand{accountNo=%d, amount='%s'}",
                accountNo, amount);
    }

}
