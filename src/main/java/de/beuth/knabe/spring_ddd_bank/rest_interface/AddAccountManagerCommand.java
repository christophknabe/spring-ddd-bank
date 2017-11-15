package de.beuth.knabe.spring_ddd_bank.rest_interface;

/**Command to add the Client with the given username as a manager to the Account with the given accountId.*/
public class AddAccountManagerCommand {

	public Long accountId;
	public String username;

    @Override
    public String toString() {
        return String.format(
                "AddAccountManagerCommand{accountId=%d, username=%s}",
                accountId, username);
    }

}
