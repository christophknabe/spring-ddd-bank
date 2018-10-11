package de.beuth.knabe.spring_ddd_bank.rest_interface;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

/** Test driver for the command and resource model classes of the REST interface layer. */
public class ModelTest {

	@Before
	public void cleanUp() {
		Locale.setDefault(Locale.GERMANY);
	}

	@Test
	public void depositCommand() {
		final DepositCommand cmd = new DepositCommand();
		cmd.accountNo = 12345789L;
		cmd.amount = 1234.56;
		assertEquals("DepositCommand{accountNo=12345789, amount='1234.56'}", cmd.toString());
	}

	@Test
	public void transferCommand() {
		final TransferCommand cmd = new TransferCommand();
		cmd.sourceAccountNo = 12345L;
		cmd.destinationAccountNo = 67890L;
		cmd.amount = 1234.56;
		assertEquals("TransferCommand{sourceAccountNo=12345, destinationAccountNo=67890, amount=1234.56}", cmd.toString());
	}

	@Test
	public void addAccountManagerCommand() {
		final AddAccountManagerCommand cmd = new AddAccountManagerCommand();
		cmd.accountNo = 12345L;
		cmd.username = "fritz";
		assertEquals("AddAccountManagerCommand{accountNo=12345, username=fritz}", cmd.toString());
	}	
	
}
