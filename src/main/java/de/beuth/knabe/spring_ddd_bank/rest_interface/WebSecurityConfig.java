package de.beuth.knabe.spring_ddd_bank.rest_interface;



import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**Configuration for securing the application.
 * @see <a href=
 * "https://spring.io/guides/gs/securing-web/"
 * >
 * Spring: Getting Started Guide "Securing a Web Application"
 * </a> 
 *      for defining users with basic authentication and for access control to URIs.
 * */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String CLIENT_ROLE = "CLIENT";
	private static final String BANK_ROLE = "BANK";

	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/bank/**").hasRole(BANK_ROLE)
            .antMatchers("/client/**").hasRole(CLIENT_ROLE)
            // For swagger-ui. See http://springfox.github.io/springfox/docs/current/#answers-to-common-questions-and-problems
            // No. 26 "Why is http://host:port/swagger-ui.html blank" ...
            .antMatchers(
                    HttpMethod.GET,
                    "/v2/api-docs",
                    "/swagger-resources/**",
                    "/swagger-ui.html**",
                    "/webjars/**",
                    "favicon.ico"
            ).permitAll()
            .anyRequest().authenticated()
            .and().httpBasic() //Authenticate with username and password.
            //For REST services disable CSRF protection. 
            //See https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html#when-to-use-csrf-protection
            .and().csrf().disable()
            ;
    }
	
	private static final List<String> predefinedUsernames = Arrays.asList("bank", "hans", "nina", "fritz", "lisa");
    
	/**Configures the {@link #predefinedUsernames} as known users with their password equal to the user name.
	 * @param auth a SecurityBuilder injected by Spring, used to create an AuthenticationManager
	 * @throws Exception if an error occurs when configuring the in memory authentication
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        final InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemoryAuthentication = auth.inMemoryAuthentication();
        for(final String username: predefinedUsernames) {
        	final String role = username.equalsIgnoreCase(BANK_ROLE) ? BANK_ROLE : CLIENT_ROLE;
			inMemoryAuthentication.withUser(username).password(username).roles(role);
        }
    }
	 * Approach taken from @see <a href="https://docs.spring.io/spring-security/reference/servlet/configuration/java.html#_hello_web_security_java_configuration">Spring Hello Web Security Java Configuration</a>  */
    
	@Bean
	public UserDetailsService userDetailsService() {
		final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		//manager.createUser(User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build());
        for(final String username: predefinedUsernames) {
        	final String role = username.equalsIgnoreCase(BANK_ROLE) ? BANK_ROLE : CLIENT_ROLE;
			//inMemoryAuthentication.withUser(username).password(username).roles(role);
			manager.createUser(User.withDefaultPasswordEncoder().username(username).password(username).roles(role).build());
        }
		return manager;
	}
    
    public List<String> predefinedUsernames(){
    	return predefinedUsernames;
    }
    
}
