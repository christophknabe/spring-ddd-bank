package de.beuth.knabe.spring_ddd_bank.infrastructure;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.beuth.knabe.spring_ddd_bank.domain.Account;
import de.beuth.knabe.spring_ddd_bank.domain.AccountAccess;
import de.beuth.knabe.spring_ddd_bank.domain.Client;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountAccessRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.ClientRepository;

/**Test driver for the {@link AccountAccessJpaRepository}*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountAccessJpaRepositoryTest {
	

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountAccessRepository accountAccessRepository;

    @Before
    public void cleanUp(){
    	accountAccessRepository.deleteAll();
    	accountRepository.deleteAll();
    	clientRepository.deleteAll();
        Locale.setDefault(Locale.GERMANY);
    }
    
    @Test
    public void isJpaRepositoryImplementation() {
    	assertEquals(AccountAccessJpaRepository.class.getName(), accountAccessRepository.getClass().getName()); 
    }

    @Test
    public void saveAndFind(){
    	//Create two clients:
        final Client jack = new Client("jack", LocalDate.parse("1966-12-31"));
        final Client anna = new Client("anna", LocalDate.parse("1977-01-01"));
        clientRepository.save(jack);
        clientRepository.save(anna);
        //Create two accounts:
        final Account jacksAccount = new Account("jack's");
        final Account annasAccount = new Account("anna's");
        accountRepository.save(jacksAccount);
        accountRepository.save(annasAccount);
        //Create two access objects:
        final AccountAccess jacksAccess = new AccountAccess(jack, true, jacksAccount);
        final AccountAccess annasAccess = new AccountAccess(anna, true, annasAccount);
        accountAccessRepository.save(jacksAccess);
        accountAccessRepository.save(annasAccess);
        
        //Successful find of managed accounts:
        final Optional<AccountAccess> jacksAccountOptional = accountAccessRepository.find(jack, jacksAccount);
        assertEquals("Jack's account found by him", true, jacksAccountOptional.isPresent());
        final Optional<AccountAccess> annasAccountOptional = accountAccessRepository.find(anna, annasAccount);
        assertEquals("Anna's account found by her", true, annasAccountOptional.isPresent());
        
        //But do not find unmanaged accounts:
        {
        	final Optional<AccountAccess> accountOptional = accountAccessRepository.find(jack, annasAccount);
            assertEquals("Anna's account found by Jack", false, accountOptional.isPresent());
        }
        {
        	final Optional<AccountAccess> accountOptional = accountAccessRepository.find(anna, jacksAccount);
            assertEquals("Jack's account found by Anna", false, accountOptional.isPresent());
        }
    }
    
}
