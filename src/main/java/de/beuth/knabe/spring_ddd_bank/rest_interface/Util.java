package de.beuth.knabe.spring_ddd_bank.rest_interface;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Util {

	/**Formats a LocalDate in Germany as 31.12.1999.*/
	static final DateTimeFormatter MEDIUM_DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

}
