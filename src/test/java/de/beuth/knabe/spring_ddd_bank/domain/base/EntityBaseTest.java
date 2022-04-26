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

package de.beuth.knabe.spring_ddd_bank.domain.base;

import de.beuth.knabe.spring_ddd_bank.domain.*;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.ClientRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.*;


/** Test driver for the entity base class {@link EntityBase}.
 * We use {@link Client} objects as example for testing the base bahavior of them. */
@RunWith(SpringRunner.class)
// @DataJpaTest
// @AutoConfigureTestDatabase
@SpringBootTest
public class EntityBaseTest {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Before
	public void cleanUpBefore() {
		clientRepository.deleteAll();
		accountRepository.deleteAll();
	}

	@After
	public void cleanUpAfter() {
		clientRepository.deleteAll();
		accountRepository.deleteAll();
	}

	@Test
	public void whenEntityHasNoId_thenItCannotBeCompared(){
		//GIVEN:
		final LocalDate birthDate = LocalDate.parse("1966-12-31");
		final Client newEntityA = new Client("jack", birthDate);
		final Client newEntityB = new Client("lisa", birthDate);
		//WHEN:
		clientRepository.save(newEntityA);
		//THEN:
		assertNull(newEntityB.getId());
		try{
			newEntityA.sameIdentityAs(newEntityB);
			fail("IllegalStateException expected");
		}catch(IllegalStateException expected){}
	}

	@Test
	public void whenEntitiesOfDifferentTypesAreCreated_thenTheyAreNotEqual(){
		final Client client = new Client("jack", LocalDate.parse("1966-12-31"));
		clientRepository.save(client);
		System.out.println(client);
		final Account account = new Account("Savings");
		accountRepository.save(account);
		System.out.println(account);
		assertEquals(false, client.equals(account));
	}

	@Test
	public void whenNewEntityObjectIsCreated_thenItHasNoIdentity() {
		final Client newEntity = new Client("jack", LocalDate.parse("1966-12-31"));
		final Long newEntityId = newEntity.getId();
		assertNull(newEntityId);
	}

	@Test
	public void when2EntitiesAreUnsaved_thenTheirHashcodesAreEqual() {
		final Client client = new Client("jack", LocalDate.parse("1966-12-31"));
		final Account account = new Account("Savings");
		assertEquals(client.hashCode(), account.hashCode());
	}

	@Test
	public void whenNewEntityObjectIsSaved_thenItGetsAUniqueIdentity() {
		//GIVEN:
		final var username = "jack";
		final LocalDate birthDate = LocalDate.parse("1966-12-31");
		final Client newEntityA = new Client(username, birthDate);
		final Client newEntityB = new Client(username, birthDate);
		//WHEN:
		clientRepository.save(newEntityA);
		clientRepository.save(newEntityB);
		//THEN:
		final Long newEntityAId = newEntityA.getId();
		final Long newEntityBId = newEntityB.getId();
		assertNotNull(newEntityAId);
		assertNotNull(newEntityBId);
		final long aId = newEntityAId;
		final long bId = newEntityBId;
		assertNotEquals(aId, bId);
	}

	@Test
	public void when2EntitiesHaveSameId_thenTheyHaveSameIdentityHashcodeAndAreEqual(){
		//GIVEN:
		final var username = "jack";
		final LocalDate birthDate = LocalDate.parse("1966-12-31");
		final Client entityA = new Client(username, birthDate);
		clientRepository.save(entityA);
		//WHEN:
		final Client entityB = clientRepository.find(username).get(); //new object
		//THEN:
		assert(entityA != entityB); //two different objects
		assertEquals(true, entityA.sameIdentityAs(entityB));
		assertEquals(true, entityB.sameIdentityAs(entityA));
		assertEquals(true, entityA.equals(entityB));
		assertEquals(true, entityB.equals(entityA));
		assertEquals(entityA.hashCode(), entityB.hashCode());
	}

	@Test
	public void whenObjectIsOfOtherType_thenEqualsReturnsFalse(){
		//GIVEN:
		final Client aObject = new Client("jack", LocalDate.parse("1966-12-31"));
		final String bObject = "Jack";
		//WHEN:
		final boolean result1 = aObject.equals(bObject);
		final boolean result2 = bObject.equals(aObject);
		//THEN:
		assertEquals(false, result1);
		assertEquals(false, result2);
		assertEquals(result1, result2); //.equals is here commutative
	}

}
