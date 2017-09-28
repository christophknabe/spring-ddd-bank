package de.beuth.knabe.spring_ddd_bank;

import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@SpringBootApplication // that is @Configuration @EnableAutoConfiguration @ComponentScan
@EnableSpringConfigured //for spring-aspects
public class Application {

	
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(final String[] args) {
		final ApplicationContext applicationContext = SpringApplication.run(Application.class);
		//displayAllBeans(applicationContext);
	}

	@Bean
	public CommandLineRunner startCommandLineRunner() {
		return args -> {
			log.info("Spring Application started.");
		};
	}
	
	private static void displayAllBeans(final ApplicationContext applicationContext) {
		System.out.println("Application.displayAllBeans:");
        final String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        for(final String beanName : allBeanNames) {
            System.out.println(beanName);
        }
    }
	

}
