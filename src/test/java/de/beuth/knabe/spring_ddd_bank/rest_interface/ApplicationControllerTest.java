package de.beuth.knabe.spring_ddd_bank.rest_interface;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import de.beuth.knabe.spring_ddd_bank.domain.Client;

/** Test driver for some utility methods of the {@link ApplicationController}. */
public class ApplicationControllerTest {

	@Before
	public void cleanUp() {
		Locale.setDefault(Locale.GERMANY);
	}

	@Test
	public void depositCommand() {
		final Client hans = new Client("hans", LocalDate.parse("1966-12-31"));
		final Client lisa = new Client("lisa", LocalDate.parse("1972-02-16"));
		final ApplicationController ctl = new ApplicationController(null);
		final ResponseEntity<ClientResource[]> result = ctl._clientsToResources(Arrays.asList(hans, lisa));
		final ClientResource hansResource = result.getBody()[0];
		final ClientResource lisaResource = result.getBody()[1];
		assertEquals(2, result.getBody().length);
		assertEquals(null, hansResource.id);
		assertEquals("hans", hansResource.username);
		assertEquals("31.12.1966", hansResource.birthDate);
		assertEquals(null, lisaResource.id);
		assertEquals("lisa", lisaResource.username);
		assertEquals("16.02.1972", lisaResource.birthDate);
	}
	
}
