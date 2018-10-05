package de.beuth.knabe.spring_ddd_bank.rest_interface;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import de.beuth.knabe.spring_ddd_bank.domain.Amount;
import de.beuth.knabe.spring_ddd_bank.domain.BankService;
import de.beuth.knabe.spring_ddd_bank.domain.Client;
import de.beuth.knabe.spring_ddd_bank.domain.imports.ClientRepository;
import de.beuth.knabe.spring_ddd_bank.rest_interface.ApplicationController.ClientCreateWithIdExc;
import multex.Exc;

/** Test driver for the command and resource model classes of the REST interface layer. */
public class ModelTest {

	@Autowired
	private ExceptionAdvice testee = new ExceptionAdvice();

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
