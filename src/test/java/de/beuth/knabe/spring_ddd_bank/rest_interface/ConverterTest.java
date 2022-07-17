package de.beuth.knabe.spring_ddd_bank.rest_interface;

import de.beuth.knabe.spring_ddd_bank.domain.Client;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/** Test driver for some utility methods of the {@link ApplicationController}. */
public class ConverterTest {

	private final Converter converter = new Converter();

	@Test
	public void clientWithoutIdToResource(){
		final Client hans = new Client("hans", LocalDate.parse("1966-12-31"));
		final ClientResource result = converter.toClientResource(hans);
		assertEquals(null, result.id);
		assertEquals("hans", result.username);
		assertEquals("1966-12-31", result.birthDate);
	}

	@Test
	public void clientWithIdToResource(){
		final Client hans = new Client("hans", LocalDate.parse("1966-12-31")){
			@Override
			public Long getId() {
				return Long.valueOf(99);
			}
		};
		final ClientResource result = converter.toClientResource(hans);
		assertEquals(Long.valueOf(99), result.id);
		assertEquals("hans", result.username);
		assertEquals("1966-12-31", result.birthDate);
	}

	@Test
	public void clientsToResources() {
		final Client hans = new Client("hans", LocalDate.parse("1966-12-31"));
		final Client lisa = new Client("lisa", LocalDate.parse("1972-02-16"));
		final var Converter = new Converter();
		final ResponseEntity<ClientResource[]> result = Converter.clientsToResources(Arrays.asList(hans, lisa));
		final ClientResource hansResource = result.getBody()[0];
		final ClientResource lisaResource = result.getBody()[1];
		assertEquals(2, result.getBody().length);
		assertEquals(null, hansResource.id);
		assertEquals("hans", hansResource.username);
		assertEquals("1966-12-31", hansResource.birthDate);
		assertEquals(null, lisaResource.id);
		assertEquals("lisa", lisaResource.username);
		assertEquals("1972-02-16", lisaResource.birthDate);
	}
	
}
