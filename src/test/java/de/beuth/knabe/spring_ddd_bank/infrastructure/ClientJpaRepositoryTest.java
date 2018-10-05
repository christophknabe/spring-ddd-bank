package de.beuth.knabe.spring_ddd_bank.infrastructure;

import static org.hamcrest.Matchers.greaterThan;
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
import org.springframework.test.context.junit4.SpringRunner;

import de.beuth.knabe.spring_ddd_bank.domain.Client;
import de.beuth.knabe.spring_ddd_bank.domain.imports.ClientRepository;

/**Test driver for the {@link ClientJpaRepository}*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientJpaRepositoryTest {
	

    @Autowired
    private ClientRepository testee;

    @Before
    public void cleanUp(){
    	testee.deleteAll();
        Locale.setDefault(Locale.GERMANY);
    }
    
    @Test
    public void isJpaRepositoryImplementation() {
    	assertEquals(ClientJpaRepository.class.getName(), testee.getClass().getName()); 
    }

    @Test
    public void findOnEmptyRepository(){    	
        {
			final List<Client> result = testee.findAll();
			assertEquals(Collections.emptyList(), result);
		}
		{
			final Optional<Client> result = testee.find(1L);
			assertEquals(Optional.empty(), result);
		}
		{
			final Optional<Client> result = testee.find("jack");
			assertEquals(Optional.empty(), result);
		}
    }

    @Test
    public void saveAndFind(){
        final Client jack = new Client("jack", LocalDate.parse("1966-12-31"));
        assertNull(jack.getId());
        final List<Client> noClients = testee.findAll();
        assertEquals(Collections.emptyList(), noClients);
        
        testee.save(jack);
        final Long jackId = jack.getId();
        assertNotNull(jackId);
        {
            final List<Client> allClients = testee.findAll();
            assertEquals(Arrays.asList(jack), allClients);        	
        }
        {
            final Client foundJack = testee.find(jackId).get();
            assertEquals(jack, foundJack);        	
        }
        {
            final Client foundJack = testee.find("jack").get();
            assertEquals(jack, foundJack);        	
        }
    }

    @Test
    public void idsAreUniqueAndAscending(){
        final Client jack = new Client("jack", LocalDate.parse("1966-12-31"));
        final Client chloe = new Client("chloe", LocalDate.parse("1977-01-01"));       
        assertNull(jack.getId());
        assertNull(chloe.getId());
        
        //save() sets a unique ID when saving an object the first time:
        testee.save(jack);       
        final Long jackId = jack.getId();
        assertNotNull(jackId);
        testee.save(chloe);
        final Long chloeId = chloe.getId();
        assertNotNull(chloeId);
        assertThat(chloeId, greaterThan(jackId));

        //Modify and save again, ID must stay the same: 
        testee.save(chloe);
        assertEquals(chloeId, chloe.getId());
    }

    @Test
    public void deleteAndFind(){
        final Client jack = new Client("jack", LocalDate.parse("1966-12-31"));
        final Client chloe = new Client("chloe", LocalDate.parse("1977-01-01"));        
        testee.save(jack);
        testee.save(chloe);
        {
            final List<Client> clients = testee.findAll(); //newest first
            assertEquals(Arrays.asList(chloe, jack), clients);        	
        }
        {
            testee.delete(chloe);
            final List<Client> clients = testee.findAll(); //newest first
            assertEquals(Arrays.asList(jack), clients);        	
        }
        {
            testee.delete(jack);
            final List<Client> clients = testee.findAll(); //newest first
            assertEquals(Arrays.asList(), clients);        	
        }
    }

    @Test
    public void findAllBornFrom(){
        final LocalDate jackBirthDate = LocalDate.parse("1966-12-31");
		final Client jack = new Client("jack", jackBirthDate);
        final LocalDate chloeBirthDate = LocalDate.parse("1977-01-01");
		final Client chloe = new Client("chloe", chloeBirthDate);        
        testee.save(jack);
        testee.save(chloe);
        {
            final List<Client> clients = testee.findAll(); //newest first
            assertEquals(Arrays.asList(chloe, jack), clients);        	
        }
        {
            final List<Client> clients = testee.findAllBornFrom(jackBirthDate); //newest first
            assertEquals(Arrays.asList(chloe, jack), clients);        	
        }
        {
            final List<Client> clients = testee.findAllBornFrom(jackBirthDate.plusDays(1)); //newest first
            assertEquals(Arrays.asList(chloe), clients);        	
        }
        {
            final List<Client> clients = testee.findAllBornFrom(chloeBirthDate); //newest first
            assertEquals(Arrays.asList(chloe), clients);        	
        }
        {
            final List<Client> clients = testee.findAllBornFrom(chloeBirthDate.plusDays(1)); //newest first
            assertEquals(Arrays.asList(), clients);        	
        }
    }

}
