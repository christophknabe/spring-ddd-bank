package de.beuth.knabe.spring_ddd_bank.infrastructure;

import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**Converts an entity attribute of type java.time.LocalDate to java.util.Date and back automatically. 
 * Then attributes of type java.time.LocalDate can be persisted.
 * This is necessary in order to persist attributes of the new types introduced in the java.time API with Java 8.
 * The older JPA 2.1 can not yet handle such attributes, but only date and time types from java.sql.
 * See article <a href="https://www.thoughts-on-java.org/persist-localdate-localdatetime-jpa/">How to persist LocalDate and LocalDateTime with JPA</a>.
 */
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {
	
    @Override
    public Date convertToDatabaseColumn(final LocalDate locDate) {
    	return (locDate == null ? null : Date.valueOf(locDate));
    }

    @Override
    public LocalDate convertToEntityAttribute(final Date sqlDate) {
    	return (sqlDate == null ? null : sqlDate.toLocalDate());
    }
    
}