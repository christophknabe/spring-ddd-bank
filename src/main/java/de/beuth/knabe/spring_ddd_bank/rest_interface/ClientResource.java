package de.beuth.knabe.spring_ddd_bank.rest_interface;

import de.beuth.knabe.spring_ddd_bank.domain.Client;

/**Data about a client of a bank. Usable as Data Transfer Object.*/
public class ClientResource {

    /**Unique ID of the client.*/
	public Long id;

    /**Complete name of the client.*/
    public String name;

	/**The birth date of the client in format 31.12.1999.*/
    public String birthDate;


    /**Necessary for Jackson*/
	public ClientResource() {}

    /**Constructs a ClientResource with the data of the passed Client entity.*/
    public ClientResource(final Client entity) {
    	this.id = entity.getId();
        this.name = entity.getUsername();
        this.birthDate = Util.MEDIUM_DATE_FORMATTER.format(entity.getBirthDate());
    }

    @Override
    public String toString() {
        return String.format(
                "Client{id=%d, name='%s', birthDate='%s'}",
                id, name, birthDate);
    }
    
}

