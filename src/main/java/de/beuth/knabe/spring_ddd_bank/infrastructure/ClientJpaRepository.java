package de.beuth.knabe.spring_ddd_bank.infrastructure;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.beuth.knabe.spring_ddd_bank.domain.Client;
import de.beuth.knabe.spring_ddd_bank.domain.imports.ClientRepository;
import de.beuth.knabe.spring_ddd_bank.infrastructure.imports.ImportedClientJpaRepository;

/**A Repository for Client entities implemented with Spring Data JPA.
 * @author Christoph Knabe
 * @since 2017-03-06
 */
@Service
public class ClientJpaRepository implements ClientRepository {

    private final ImportedClientJpaRepository impl;

    @Autowired
    public ClientJpaRepository(final ImportedClientJpaRepository impl) {
        this.impl = impl;
    }

    public void deleteAll(){impl.deleteAll();}

    public Client save(final Client client){
        return impl.save(client);
    }

    public void delete(Client client){
        impl.delete(client);
    }

    public List<Client> findAll(){
        return impl.findAllByOrderByIdDesc();
    }

    public List<Client> findAllBornFrom(final LocalDate minDate){
        return impl.findAllByBirthDateGreaterThanEqualOrderByBirthDateDescIdDesc(minDate);
    }

}
