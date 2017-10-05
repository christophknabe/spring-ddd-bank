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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**Test driver for the service class {@linkplain BankService}*/
@RunWith(SpringRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase
@SpringBootTest
public class BankServiceTest {

    /**Only for use in the cleanUp method!*/
    @Autowired
    private CleanupService cleanupService;

    @Autowired
    private BankService bankService;

    @Before
    public void cleanUp(){
        cleanupService.deleteAll();
    }

    @Test
    public void findAndCreateClient(){
		//Before createClient no clients can be found:
        final Iterable<Client> noClients = bankService.findAllClients();
        _assertEmpty(noClients);
        
        //Create a Client:
        final LocalDate jackBirthDate = LocalDate.parse("1966-12-31");
		final Client newClient = bankService.createClient("Jack Bauer", jackBirthDate);
		//By createClient a new Client object was created with an id:
        final Long clientId = newClient.getId();
		assertNotNull(clientId);

		//By this id we always can retrieve the Client:
		final Optional<Client> optionalClient = bankService.findClient(clientId);
		assertNotNull(optionalClient);
		assertEquals(true, optionalClient.isPresent());
		final Client client = optionalClient.get();
		assertEquals(clientId, client.getId());
		assertEquals("Jack Bauer", client.getName());
		assertEquals(jackBirthDate, client.getBirthDate());
    }

    @Test
    public void createNothingFindAllClients() {
        final Iterable<Client> noClients = bankService.findAllClients();
        _assertEmpty(noClients);
    }

	private void _assertEmpty(final Iterable<Client> clients) {	
		int count = 0;
		for(final Client c: clients) {
			count++;
		}
		assertEquals("Iterable<Client> should be empty.", 0, count);
	}

    @Test
    public void createClientsFindAllClients() {
        bankService.createClient("Kim Bauer", LocalDate.parse("1994-05-21"));
        bankService.createClient("Jack Bauer", LocalDate.parse("1966-12-31"));
        final Iterable<Client> clients = bankService.findAllClients();
        final String result = stringize(clients);
        assertEquals("1966-12-31 Jack Bauer, 1994-05-21 Kim Bauer", result);
    }

    @Test
    public void findYoungClients() {
        bankService.createClient("Kim Bauer", LocalDate.parse("1994-05-21"));
        bankService.createClient("Jack Bauer", LocalDate.parse("1966-12-31"));
        bankService.createClient("Chloe O'Brian", LocalDate.parse("1992-12-01"));
        {
            //Expecting no clients:
            final Iterable<Client> noClients = bankService.findYoungClients(LocalDate.parse("1994-05-22"));
            final String result = stringize(noClients);
            assertEquals("", result);
        }
        {
            //Expecting one client:
            final Iterable<Client> oneClients = bankService.findYoungClients(LocalDate.parse("1994-05-21"));
            final String result = stringize(oneClients);
            assertEquals("1994-05-21 Kim Bauer", result);
        }
        {
            //Expecting two clients:
            final Iterable<Client> twoClients = bankService.findYoungClients(LocalDate.parse("1992-12-01"));
            final String result = stringize(twoClients);
            assertEquals("1994-05-21 Kim Bauer, 1992-12-01 Chloe O'Brian", result);
        }
        {
            //Expecting still two clients:
            final Iterable<Client> twoClients = bankService.findYoungClients(LocalDate.parse("1967-01-01"));
            final String result = stringize(twoClients);
            assertEquals("1994-05-21 Kim Bauer, 1992-12-01 Chloe O'Brian", result);
        }
        {
            //Expecting three clients:
            final Iterable<Client> twoClients = bankService.findYoungClients(LocalDate.parse("1966-12-31"));
            final String result = stringize(twoClients);
            assertEquals("1994-05-21 Kim Bauer, 1992-12-01 Chloe O'Brian, 1966-12-31 Jack Bauer", result);
        }
    }

    @Test
    public void findRichClients() {
        final Client kim = bankService.createClient("Kim Bauer", LocalDate.parse("1994-05-21"));
        final Client chloe = bankService.createClient("Chloe O'Brian", LocalDate.parse("1992-12-01"));
        assertTrue(chloe.getId() > kim.getId());
        final AccountAccess kimAccount = kim.createAccount("Kim's Account");
        final AccountAccess chloeAccount = chloe.createAccount("Chloe's Account");
        kim.deposit(kimAccount.getAccount(), new Amount(1000,01));
        chloe.deposit(chloeAccount.getAccount(), new Amount(1000,00));
        {
            //Expecting no clients:
            final Iterable<Client> noClients = bankService.findRichClients(new Amount(1000,02));
            final String result = stringize(noClients);
            assertEquals("", result);
        }
        {
            //Expecting one client:
            final Iterable<Client> oneClients = bankService.findRichClients(new Amount(1000,01));
            final String result = stringize(oneClients);
            assertEquals("1994-05-21 Kim Bauer", result);
        }
        {
            //Expecting two clients, the richer should come first:
            final Iterable<Client> twoClients = bankService.findRichClients(new Amount(1000,00));
            final String result = stringize(twoClients);
            assertEquals("1994-05-21 Kim Bauer, 1992-12-01 Chloe O'Brian", result);
        }
        chloe.deposit(chloeAccount.getAccount(), new Amount(0,01));
        {
            //Expecting two equally rich clients. The entity with the higher ID should be first.:
            final Iterable<Client> twoClients = bankService.findRichClients(new Amount(1000,01));
            final String result = stringize(twoClients);
            assertEquals("1992-12-01 Chloe O'Brian, 1994-05-21 Kim Bauer", result);
        }
    }


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
