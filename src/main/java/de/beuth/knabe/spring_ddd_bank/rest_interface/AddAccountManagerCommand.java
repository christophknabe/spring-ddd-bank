package de.beuth.knabe.spring_ddd_bank.rest_interface;

/**Command to add the Client with the given managerClientId as a manager to the Account with the given accountId.*/
public class AddAccountManagerCommand {

	public Long accountId;
	public Long managerClientId;

    @Override
    public String toString() {
        return String.format(
                "AddAccountManagerCommand{accountId=%d, managerClientId=%d}",
                accountId, managerClientId);
    }

}
