package de.beuth.knabe.spring_ddd_bank.domain;

import de.beuth.knabe.spring_ddd_bank.domain.base.EntityBase;
import de.beuth.knabe.spring_ddd_bank.domain.imports.AccountRepository;
import de.beuth.knabe.spring_ddd_bank.domain.imports.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class Client extends EntityBase<Client>{

    private String name;
    private LocalDate birthDate;

    /**Necessary for JPA object recreation when reading from database*/
    Client() {}

    public Client(final String name, final LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return String.format(
                "Client{id=%d, name='%s', birthDate='%s'}",
                getId(), name, birthDate);
    }

	public String getName() {
		return name;
	}

    public LocalDate getBirthDate() {
        return birthDate;
    }
}

