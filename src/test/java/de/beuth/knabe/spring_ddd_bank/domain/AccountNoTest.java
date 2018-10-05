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
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

/** Test driver for the value object {@link AccountNo} */
public class AccountNoTest {

	@Before
	public void setUp() {
		Locale.setDefault(Locale.GERMANY);
	}

	@Test
	public void constructExtract() {
		{
			final AccountNo result = new AccountNo(0L);
			assertEquals(Long.valueOf(0), result.toLong());
			assertEquals("0", result.toString());
		}
		{
			final AccountNo result = new AccountNo(Long.MAX_VALUE);
			assertEquals(Long.valueOf(Long.MAX_VALUE), result.toLong());
			assertEquals(Long.toString(Long.MAX_VALUE), result.toString());
		}
	}

	@Test
	public void constructIllegals() {
		try {
			new AccountNo((Long)null);
			fail("AccountNo.IllegalExc expected");
		} catch (AccountNo.IllegalExc expected) {
		}
		try {
			new AccountNo((String)null);
			fail("AccountNo.IllegalExc expected");
		} catch (AccountNo.IllegalExc expected) {
		}
		try {
			new AccountNo("");
			fail("AccountNo.IllegalExc expected");
		} catch (AccountNo.IllegalExc expected) {
		}
		try {
			new AccountNo("A");
			fail("AccountNo.IllegalExc expected");
		} catch (AccountNo.IllegalExc expected) {
		}
		try {
			new AccountNo(".");
			fail("AccountNo.IllegalExc expected");
		} catch (AccountNo.IllegalExc expected) {
		}
	}
	
}
