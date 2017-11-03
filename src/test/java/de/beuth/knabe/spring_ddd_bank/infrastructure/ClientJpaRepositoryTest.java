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

package de.beuth.knabe.spring_ddd_bank.infrastructure;

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

/**Test driver for the {@linkplain ClientJpaRepository}*/
@RunWith(SpringRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase
@SpringBootTest
public class ClientJpaRepositoryTest {
	

    @Autowired
    private ClientJpaRepository testee;

    @Before
    public void cleanUp(){
    	testee.deleteAll();
        Locale.setDefault(Locale.GERMANY);
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
    }

    @Test
    public void saveAndFind(){
        final Client jack = new Client("Jack Bauer", LocalDate.parse("1966-12-31"));
        assertNull(jack.getId());
        final List<Client> noClients = testee.findAll();
        assertEquals(Collections.emptyList(), noClients);
        
        testee.save(jack);
        {
            final List<Client> allClients = testee.findAll();
            assertEquals(Arrays.asList(jack), allClients);
        	
        }
    }
    
    /*
     * The test coverage should be improved by adding more test cases 
     * including all methods of the testee.
     */

}
