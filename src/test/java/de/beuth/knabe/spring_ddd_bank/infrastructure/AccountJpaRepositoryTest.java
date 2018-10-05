package de.beuth.knabe.spring_ddd_bank.infrastructure;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.beuth.knabe.spring_ddd_bank.domain.Account;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountRepository;

/**Test driver for the {@link ClientJpaRepository}*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountJpaRepositoryTest {
	

    @Autowired
    private AccountRepository testee;

    @Before
    public void cleanUp(){
    	testee.deleteAll();
        Locale.setDefault(Locale.GERMANY);
    }
    
    @Test
    public void isJpaRepositoryImplementation() {
    	assertEquals(AccountJpaRepository.class.getName(), testee.getClass().getName()); 
    }

    @Test
    public void saveCreatesAscendingAccountNumbers(){    
        final Account jacksAccount = new Account("jack's");
        final Account annasAccount = new Account("anna's");
        assertNull(jacksAccount.getId());
        assertNull(annasAccount.getId());
        
        //The first save() for an account creates an ascending ID and account number:
        testee.save(jacksAccount);
        final Long jacksAccountId = jacksAccount.getId();
		assertNotNull(jacksAccountId);
        testee.save(annasAccount);
        final Long annasAccountId = annasAccount.getId();
		assertNotNull(annasAccountId);
		assertThat(annasAccountId, greaterThan(jacksAccountId));
		
		//The accountNo is always the same as the accountId:
		assertEquals(annasAccountId, annasAccount.accountNo().toLong());
		assertEquals(jacksAccountId, jacksAccount.accountNo().toLong());
    }

}
