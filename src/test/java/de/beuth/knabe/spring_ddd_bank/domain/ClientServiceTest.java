/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.beuth.knabe.spring_ddd_bank.domain;

import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountAccessRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.ClientRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase
@SpringBootTest
public class ClientServiceTest {


    /**Only for use in the cleanUp method!*/
    @Autowired
    private CleanupService cleanupService;

    @Autowired
    private BankService bankService;

    @Autowired
    private ClientService clientService;

    @Before
    public void cleanUp(){
        cleanupService.deleteAll();
        Locale.setDefault(Locale.GERMANY);
    }

    @Test
    public void createAccountCheckProperties(){
        final Client jack = bankService.createClient("Jack Bauer", LocalDate.parse("1966-12-31"));
        final AccountAccess jacksSavings = clientService.createAccount(jack, "Jack's Savings");
        assertNotNull(jacksSavings);
        assertEquals(jack, jacksSavings.getClient());
        assertEquals("Jack Bauer", jacksSavings.getClient().getName());
        assertEquals(true, jacksSavings.isOwner());
        final Account jacksSavingsAccount = jacksSavings.getAccount();
        assertEquals("Jack's Savings", jacksSavingsAccount.getName());
        assertEquals(0.0, jacksSavingsAccount.getBalance().toDouble(), 0.001);
        assertEquals(jack.getId(), jacksSavings.getClient().getId());
        assertEquals(true, jack.sameIdentityAs(jacksSavings.getClient()));
        final String report = clientService.getAccountsReport(jack);
        assertEquals("Accounts of client: Jack Bauer\n" +
                "isOwner \t 0,00\tJack's Savings\n", report);
    }

    @Test
    public void deposit(){
        final Client jack = bankService.createClient("Jack Bauer", LocalDate.parse("1966-12-31"));
        final AccountAccess jacksGiro = clientService.createAccount(jack, "Jack's Giro");
        clientService.deposit(jacksGiro.getAccount(), new Amount(999999999,99));
        final String report = clientService.getAccountsReport(jack);
        assertEquals("Accounts of client: Jack Bauer\nisOwner \t999999999,99\tJack's Giro\n", report);
    }

    @Test
    public void depositAmountExc(){
        final Client jack = bankService.createClient("Jack Bauer", LocalDate.parse("1966-12-31"));
        final Account jacksGiro = clientService.createAccount(jack, "Jack's Giro").getAccount();
        try {
            clientService.deposit(jacksGiro, Amount.ZERO);
            fail("ClientService.AmountExc expected");
        } catch (ClientService.AmountExc expected) {}
        final String report = clientService.getAccountsReport(jack);
        assertEquals("Accounts of client: Jack Bauer\nisOwner \t 0,00\tJack's Giro\n", report);
    }

    @Test
    public void depositsReport(){
        final Client jack = bankService.createClient("Jack Bauer", LocalDate.parse("1966-12-31"));
        final AccountAccess jacksGiro = clientService.createAccount(jack, "Jack's Giro");
        final AccountAccess jacksSavings = clientService.createAccount(jack, "Jack's Savings");
        clientService.deposit(jacksGiro.getAccount(), new Amount(999999999,99));
        clientService.deposit(jacksSavings.getAccount(), new Amount(0,1));
        final String report = clientService.getAccountsReport(jack);
        assertEquals("Accounts of client: Jack Bauer\n" +
                "isOwner \t 0,01\tJack's Savings\n" +
                "isOwner \t999999999,99\tJack's Giro\n", report);
    }

    @Test
    public void transfer(){
        final Client jack = bankService.createClient("Jack Bauer", LocalDate.parse("1966-12-31"));
        final Account jacksGiro = clientService.createAccount(jack, "Jack's Giro").getAccount();
        final Account jacksSavings = clientService.createAccount(jack, "Jack's Savings").getAccount();
        final Amount minimumBalance = ClientService.getMinimumBalance();
        final Amount maximumTransferAmount = minimumBalance.times(-1);
        clientService.transfer(jack, jacksGiro, jacksSavings, maximumTransferAmount);
        final String report = clientService.getAccountsReport(jack);
        final String maximumTransferAmountString = maximumTransferAmount.toString();
        assertEquals("Accounts of client: Jack Bauer\n" +
                "isOwner \t" + maximumTransferAmountString + "\tJack's Savings\n" +
                "isOwner \t-" + maximumTransferAmountString + "\tJack's Giro\n", report);
    }

    @Test
    public void transferExc(){
        final Client jack = bankService.createClient("Jack Bauer", LocalDate.parse("1966-12-31"));
        final Account jacksGiro = clientService.createAccount(jack, "Jack's Giro").getAccount();
        final Account jacksSavings = clientService.createAccount(jack, "Jack's Savings").getAccount();
        try{
            clientService.transfer(jack, jacksGiro, jacksSavings, new Amount(0));
            fail("ClientService.AmountExc expected");
        }catch (ClientService.AmountExc expected){}

        final Client chloe = bankService.createClient("Chloe O'Brian", LocalDate.parse("1992-12-01"));
        try{
            clientService.transfer(chloe, jacksGiro, jacksSavings, new Amount(0,01));
            fail("ClientService.WithoutRightExc expected");
        }catch (ClientService.WithoutRightExc expected){}

        final Amount minimumBalance = ClientService.getMinimumBalance();
        final Amount maximumTransferAmount = minimumBalance.times(-1);
        final Amount tooGreatAmount = maximumTransferAmount.plus(new Amount(0, 01));
        try{
            clientService.transfer(jack, jacksGiro, jacksSavings, tooGreatAmount);
            fail("ClientService.MinimumBalanceExc expected");
        }catch (ClientService.MinimumBalanceExc expected){}
    }
    
    @Test
    public void addAccountManager(){
        final Client jack = bankService.createClient("Jack Bauer", LocalDate.parse("1966-12-31"));
        final Account jacksGiro = clientService.createAccount(jack, "Jack's Giro").getAccount();
        final Account jacksSavings = clientService.createAccount(jack, "Jack's Savings").getAccount();

        final Client chloe = bankService.createClient("Chloe O'Brian", LocalDate.parse("1992-12-01"));
        //Client cannot transfer from an account, if she is not manager of the account:
        try{
            clientService.transfer(chloe, jacksGiro, jacksSavings, new Amount(0,01));
            fail("ClientService.WithoutRightExc expected");
        }catch (ClientService.WithoutRightExc expected){}
        
        //Client cannot add an account manager, if she is not owner of the account:
        try {
			chloe.addAccountManager(jacksGiro, chloe);
            fail("Client.NotOwnerExc expected");
		} catch (Client.NotOwnerExc expected){}
        
        jack.addAccountManager(jacksGiro, chloe);
        //Now chloe can transfer from jacksGiro account:
        clientService.transfer(chloe, jacksGiro, jacksSavings, new Amount(0,01));
        
        //But chloe is only manager of the account, not owner. She cannot add another Manager to the account.
        final Client tony = bankService.createClient("Tony Almeida", LocalDate.parse("1964-03-22"));
        try {
			chloe.addAccountManager(jacksGiro, tony);
            fail("Client.NotOwnerExc expected");
		} catch (Client.NotOwnerExc expected){}    	
    }

    //TODO Test against double adding of a Client as manager to the same Account. 17-08-18

    /**Makes a String representation of all passed clients, separated by commas. The format of a 2 client result will be "1999-12-31 name1, 2017-12-31 name2".*/
    private String stringize(Iterable<Client> clients) {
        final StringBuilder result = new StringBuilder();
        for(final Client client: clients){
            if(result.length() > 0){
                result.append(", ");
            }
            assertNotNull(client.getId());
            result.append(client.getBirthDate());
            result.append(' ');
            result.append(client.getName());
        }
        return result.toString();
    }

}
