package de.beuth.knabe.spring_ddd_bank.rest_interface;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.VndErrors;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.beuth.knabe.spring_ddd_bank.Application;

/**Base class for tests of REST service. Derived from 
 * https://www.tutorialspoint.com/spring_boot/spring_boot_rest_controller_unit_test.htm */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public abstract class AbstractRestControllerTest {
	
   protected MockMvc mvc;
   @Autowired
   WebApplicationContext webApplicationContext;

   @Before
   public void setUp() {
	   System.out.println(getClass().getName() + ": mvc=" + mvc);
       mvc = MockMvcBuilders
               .webAppContextSetup(webApplicationContext)
               // See https://docs.spring.io/spring-security/site/docs/4.0.x/reference/htmlsingle/#running-as-a-user-in-spring-mvc-test-with-annotations
               //.apply(springSecurity())//TODO Make Spring Security in tests work
               .build();
	   System.out.println(getClass().getName() + ": mvc=" + mvc);
   }
   protected String mapToJson(final Object obj) throws JsonProcessingException {
      final ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(obj);
   }
   protected <T> T mapFromJson(final String json, final Class<T> clazz)
      throws JsonParseException, JsonMappingException, IOException {
      
      final ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(json, clazz);
   }
   protected void assertSuccess(final String message, MockHttpServletResponse response) throws IOException {
       final int status = response.getStatus();
	   if(status >= 300) {
           final var content = response.getContentAsString();
           final VndErrors vndErrors = mapFromJson(content, VndErrors.class);
		   fail("Request failed, " + message + ", status=" + status + ": " + vndErrors);
	   }
   }
   
}