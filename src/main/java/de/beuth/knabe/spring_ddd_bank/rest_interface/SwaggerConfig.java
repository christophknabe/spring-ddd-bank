package de.beuth.knabe.spring_ddd_bank.rest_interface;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;

/**
 * Swagger Configuration for REST-API documentation.
 *
 * For details refer to:
 * - http://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
 * - https://springframework.guru/spring-boot-restful-api-documentation-with-swagger-2/
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private WebSecurityConfig webSecurityConfig;

    public SwaggerConfig(final WebSecurityConfig webSecurityConfig) {
        this.webSecurityConfig = webSecurityConfig;
    }

    @Bean
    public Docket swaggerConfiguration() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.or(
                        PathSelectors.regex("/bank.*"),
                        PathSelectors.regex("/client.*")
                ))
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(Arrays.asList(new BasicAuth("basicAuth")));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Spring DDD Bank API",
                "A sample project following Domain Driven Design with Spring Data JPA" +
                        "<br />Predefined users are " + webSecurityConfig.predefinedUsernames() +
                        "<br />Passwords are equal to usernames." +
                        "<br />Keep in mind to login via the Authorize button before calling API methods.",
                null,
                null,
                new Contact(
                        "Christoph Knabe", null, null
                ),
                null,
                null,
                Collections.emptyList()
        );
    }
}
