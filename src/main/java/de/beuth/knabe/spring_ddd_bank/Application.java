package de.beuth.knabe.spring_ddd_bank;

import de.beuth.knabe.spring_ddd_bank.domain.BankService;
import de.beuth.knabe.spring_ddd_bank.domain.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootApplication // that is @Configuration @EnableAutoConfiguration @ComponentScan
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(final String[] args) {
		SpringApplication.run(Application.class);
	}

	@Bean
	public CommandLineRunner start() {
		return args -> {
			log.info("Spring Application started.");
		};
	}

}
