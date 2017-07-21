Project Spring DDD Bank
=======================
A sample project following Domain Driven Design with Spring Data JPA

                                                          (C) Christoph Knabe, 2017-03-17

In this project I am trying to apply principles of Domain Driven Design.
In contrary to full-blown DDD examples on the web I am applying here some simplifications.
This sample application is used for a course on Software Engineering at Beuth University of Applied Sciences Berlin.

This project uses

- JDK 8
- Maven 3
- Spring Boot
- Spring Data + JPA + Hibernate + Derby
- JUnit 4
- The Exception Handling and Reporting Framework MulTEx

Detailed version indications you can find in the file `pom.xml`.

By  `mvn clean test`   all necessary libraries will be fetched, the project will be compiled, exception message texts will be collected and the test suite will be executed.

After this is working you can import the Maven project into your Java IDE.

## Which DDD principles are implemented?
- Modelling the domain layer as one package, which does not depend on any other package besides standard Java SE packages as `java.time` and `javax.persistence`. The latter only for the JPA annotations.
- Referencing required services only by self-defined, minimal interfaces (in package `domain.imports`).
- Implementing required services in the infrastructure layer (in package `infrastructure`).
- Linking together required services and their implementations by Dependency Injection.

## Other Characteristics
- It is a little bank application, where the bank can create clients and clients can create and manage accounts, e.g. deposit and transfer money.
- The analysis class diagram is in file `src/main/BankModel.pdf`. Its editable source by UMLet has extension `.uxf`.
- Internationalizable, parameterizable exception message texts
- Capture of each exception message text in the reference language directly as main JavaDoc comment of the exception
- Tests are run against an empty in-memory Derby database.
- Generation of a test coverage report by the [JaCoCo Maven plugin](http://www.eclemma.org/jacoco/trunk/doc/maven.html) into `/target/site/jacoco-ut/index.html`.

### Where are the exception message texts?
In the file `MessageText.properties`. The editable original is in `src/main/resources/`.
During Maven phase `compile` it is copied to `target/classes/`.
During the following phase `process-classes` the exception message texts are extracted from the JavaDoc comments of all exceptions under `src/main/java/`
by the  `ExceptionMessagesDoclet`  as configured for the `maven-javadoc-plugin`. They are appended to the message text file in `target/classes/`.


## Plans

- Make the domain model less anemic by moving the methods of `ClientService` into class `Client`. This requires the feature **Domain Object Dependency Injection** (DODI), which can only be implemented by using full AspectJ compile-time weaving. Still needs research.
- Nice to have: Avoid own ID of `AccountAccess`, because this class models an m:n association between `Client` and `Account`. There should not be a possibility for several links between a client and an account. This would require the usage of `client.id` and `account.id` as a composite ID for `AccountAccess`. Not so easy, see http://stackoverflow.com/questions/18739334/jpa-how-to-associate-entities-with-relationship-attributes
- Implementation of real unit tests with mock implementations of the repository interfaces.
- Put an application layer with transaction management on top of the domain model.
- Export the application layer as a REST service.

## References and Sources
- [Detailed example text about DDD](https://www.mirkosertic.de/blog/2013/04/domain-driven-design-example/)
- [The DDD Sample project](https://github.com/citerus/dddsample-core), from which are taken some inspirations
- [The Ports and Adapters Pattern](http://alistair.cockburn.us/Hexagonal+architecture)
- [Can DDD be Adequately Implemented Without DI and AOP?](https://www.infoq.com/news/2008/02/ddd-di-aop)
- [Spring Boot](https://spring.io/guides/gs/spring-boot/)
- [Spring Dependency Injection](http://projects.spring.io/spring-framework/)
- [Spring Data JPA](https://spring.io/guides/gs/accessing-data-jpa/)
- [Spring Data JPA Query Methods](http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods)
- This application implements my knowledge about layering and data access at a given point of time. Previous versions were in a 3-layer form in german under [Bank3Tier - Bankanwendung in 3 Schichten](http://public.beuth-hochschule.de/~knabe/java/bank3tier/).
