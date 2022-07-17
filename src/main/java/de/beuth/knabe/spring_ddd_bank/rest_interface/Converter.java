package de.beuth.knabe.spring_ddd_bank.rest_interface;

import de.beuth.knabe.spring_ddd_bank.domain.Client;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.stream.Stream;

public class Converter {

	/** Formats a LocalDate as in 1999-12-31. */
	private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

	String toString(final LocalDate localDate){
		return ISO_DATE_FORMATTER.format(localDate);
	}

	LocalDate toLocaldate(final String isoDateString){
		return LocalDate.parse(isoDateString, ISO_DATE_FORMATTER);
	}

	/**
	 * Constructs a ClientResource with the data of the passed Client entity.
	 *
	 * @param entity
	 *            the entity to be converted
	 */
	public ClientResource toClientResource(final Client entity) {
		final ClientResource result = new ClientResource(entity.getId(), entity.getUsername(), toString(entity.getBirthDate()));
		return result;
	}

	ResponseEntity<ClientResource[]> clientsToResources(final List<Client> clients) {
		final Stream<ClientResource> result = clients.stream().map(this::toClientResource);
		final ClientResource[] resultArray = result.toArray(ClientResource[]::new);
		return new ResponseEntity<>(resultArray, HttpStatus.OK);
	}

}
