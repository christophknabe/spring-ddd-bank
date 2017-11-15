package de.beuth.knabe.spring_ddd_bank.infrastructure.imports;

import de.beuth.knabe.spring_ddd_bank.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**Required Spring JPA repository for clients. The methods are named according to the Spring Data JPA convention.
 * They can be implemented by Spring during bean creation, but can be implemented independently of Spring, too.
 * @author Christoph Knabe
 * @since 2017-03-03
 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation">Spring Data Query Methods</a>
 */
public interface ImportedClientJpaRepository extends JpaRepository<Client, Long> {

    /**Deletes all Clients. Useful for test scenarios in order to start with an empty client set*/
    void deleteAll();

    Client save(Client client);

    void delete(Client client);
    
    Optional<Client> findOneById(Long id);
    
    Optional<Client> findOneByUsername(String username);
    
    Optional<Client> findOneByUsernameAndBirthDate(String name, LocalDate birthDate);

    List<Client> findAllByOrderByIdDesc();

    List<Client> findAllByBirthDateGreaterThanEqualOrderByBirthDateDescIdDesc(LocalDate minDate);

    Optional<Client> findFirstByOrderByIdAsc();
}
