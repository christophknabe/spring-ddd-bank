package de.beuth.knabe.spring_ddd_bank.rest_interface;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import de.beuth.knabe.spring_ddd_bank.domain.Account;
import de.beuth.knabe.spring_ddd_bank.domain.AccountAccess;
import de.beuth.knabe.spring_ddd_bank.domain.Amount;
import de.beuth.knabe.spring_ddd_bank.domain.BankService;
import de.beuth.knabe.spring_ddd_bank.domain.Client;
import multex.Exc;
import static multex.MultexUtil.create;

/**A Spring Web MVC controller offering a REST service for accessing all external operations of the application.*/
//Made transactional according to the answer of Rogério at https://stackoverflow.com/questions/23118789/why-we-shouldnt-make-a-spring-mvc-controller-transactional
@Transactional @RestController
public class ApplicationController {

	private final BankService bankService;
    
	private final String className = getClass().getSimpleName();
	
    @Autowired
    public ApplicationController(final BankService bankService) {
    	this.bankService = bankService;
	}
    /*
     * A good resource for the design of REST URIs is https://blog.mwaysolutions.com/2014/06/05/10-best-practices-for-better-restful-api/ 
     * But for simplification of access control we will group the URIs by the roles, which may access them.
     * So URIs starting with /bank are for bankers, URIs starting with /client are for clients.
     * For further simplification we will not include the username of a Client into his URI, 
     * but each request to a URI starting with /client will infer the concerned username from the authenticated user. 
     */

    //For everyone (guests):
    
    @GetMapping(path="/")
    public ResponseEntity<String> home(final WebSecurityConfig config, final HttpMethod method, final WebRequest request) {
		_print(method, request);
        final ResponseEntity<String> responseEntity = new ResponseEntity<>("Welcome to the Banking Web App. Predefined users with empty passwords are " + config.predefinedUsernames() + ". Use URIs under /bank/ or /client/", HttpStatus.OK);
		return responseEntity;
    }
    
    //For the banker role all URIs under /bank:

    /*A transaction, which creates two random objects of type Client, but sometimes fails after the first one.*/
    @PostMapping("/bank/pair")
    public ResponseEntity<ClientResource[]> create2Clients(final HttpMethod method, final WebRequest request) {
		_print(method, request);
    	final long now = System.currentTimeMillis();
    	final long number = now % 100;
    	final Client client1 = bankService.createClient("hans"+number, _randomClientBirthDate());
    	System.out.printf("Client %s created.\n", client1);
    	if(number%3 == 0) {
    		throw new Exc("Exception after creating {0}. Should have been rolled back.", client1);
    	}
    	final Client client2 = bankService.createClient("jana"+number, _randomClientBirthDate());
    	System.out.printf("Client %s created.\n", client2);
    	final List<Client> clients = bankService.findAllClients();
        return _clientsToResources(clients);
    }
    
    @PostMapping("/bank/client")
    public ResponseEntity<ClientResource> createClient(
    		@RequestBody  final ClientResource clientResource,
    		final HttpMethod method, final WebRequest request
    		){
		_print(method, request);
		if(clientResource.id != null) {
			throw create(ClientCreateWithIdExc.class, clientResource.name, clientResource.id);
		}
    	final LocalDate birthLocalDate = LocalDate.parse(clientResource.birthDate, Util.MEDIUM_DATE_FORMATTER);
		final Client client = bankService.createClient(clientResource.name, birthLocalDate);
        return new ResponseEntity<>(new ClientResource(client), HttpStatus.CREATED);
    }
    
    /**The client to be created with username {0} must not have an ID, but has {1}*/
    public static class ClientCreateWithIdExc extends multex.Exc {}

    @DeleteMapping("/bank/client/{username}")
    public ResponseEntity<String> deleteClient(
    		@PathVariable  final String username,
    		final HttpMethod method, final WebRequest request
    		){
		_print(method, request);
    	final Client client = bankService.findClient(username).get();
    	bankService.deleteClient(client);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }	
    
    @GetMapping(path="/bank/client")
    public ResponseEntity<ClientResource[]> findClients(
    		@RequestParam(name="fromBirth", defaultValue="") final String fromBirth,
    		@RequestParam(name="minBalance", defaultValue="") final String minBalance,
    		final HttpMethod method, final WebRequest request
    		){
		_print(method, request);
    	final List<Client> clients;
    	if("".equals(fromBirth) && "".equals(minBalance)) {
    	    clients = bankService.findAllClients();
    	}else if("".equals(minBalance)) { //only fromBirth given
        	final LocalDate fromBirthLocalDate = LocalDate.parse(fromBirth, Util.MEDIUM_DATE_FORMATTER);
    		clients = bankService.findYoungClients(fromBirthLocalDate);
    	}else if(fromBirth.equals("")) { //only minBalance given
        	final double minBalanceDouble = Double.parseDouble(minBalance);
        	final Amount minBalanceAmount = new Amount(minBalanceDouble);
    		clients = bankService.findRichClients(minBalanceAmount);
    	}else {
    		throw new Exc("Must not provide both parameters: fromBirth and minBalance!");
    	}
        return _clientsToResources(clients);
    }
    
    //For the client role all URIs under /client:
    
    @PostMapping("/client/account")
    public ResponseEntity<AccountAccessResource> createAccount(
    		@RequestBody  final String accountName,
    		final HttpMethod method, final WebRequest request
    		){
		_print(method, request);
		final Client client = _findClient(request);
    	final AccountAccess r = client.createAccount(accountName);
		final AccountAccessResource result = new AccountAccessResource(r);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    
    /*Resource for a coarse grained business process according to https://www.thoughtworks.com/de/insights/blog/rest-api-design-resource-modeling*/
    @PostMapping("/client/deposit")
    public ResponseEntity<Void> deposit(@RequestBody  final DepositCommand command,
    		final HttpMethod method, final WebRequest request
    		){
		_print(method, request);
		final Client client = _findClient(request);
    	final Account destinationAccount = client.findAccount(command.accountId).get();
    	final Amount amount = new Amount(command.amount);
    	client.deposit(destinationAccount, amount);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*Resource for a coarse grained business process according to https://www.thoughtworks.com/de/insights/blog/rest-api-design-resource-modeling*/
    @PostMapping("/client/transfer")
    public ResponseEntity<AccountResource> transfer(@RequestBody  final TransferCommand command,
    		final HttpMethod method, final WebRequest request
    		){
		_print(method, request);
		final Client client = _findClient(request);
    	final Account sourceAccount = client.findAccount(command.sourceAccountId).get();
    	final Account destinationAccount = client.findAccount(command.destinationAccountId).get();
    	final Amount amount = new Amount(command.amount);
    	client.transfer(sourceAccount, destinationAccount, amount);
    	final AccountResource result = new AccountResource(sourceAccount);
        return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
    }    

    /*Resource for a coarse grained business process according to https://www.thoughtworks.com/de/insights/blog/rest-api-design-resource-modeling*/
    @PostMapping("/client/manager")
    public ResponseEntity<AccountAccessResource> addAccountManager(
    		@RequestBody  final AddAccountManagerCommand command,
    		final HttpMethod method, final WebRequest request
    		){
		_print(method, request);
		final Client client = _findClient(request);
    	final Account account = client.findAccount(command.accountId).get();
    	final Client manager = bankService.findClient(command.username).get();
    	final AccountAccessResource result = new AccountAccessResource(client.addAccountManager(account, manager));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    } 

    @GetMapping("/client/account")
    public ResponseEntity<String> accountsReport(
    		final HttpMethod method, final WebRequest request
    		){
		_print(method, request);
		final Client client = _findClient(request);
    	final String result = client.accountsReport();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }    
    
    /**Prints a message containing the current class name, the HTTP method, and infos about the current request.*/
	private void _print(final HttpMethod method, final WebRequest request) {
		System.out.printf("%s %s %s\n", className, method, request);
	}

    /**Returns a random birth date ranging from 18 years before now to 100 years before now.*/
	private LocalDate _randomClientBirthDate() {
		final long nowEpochDay = LocalDate.now().toEpochDay();
		final int minYears = 18;
		final int maxYears = 100;
		final long minEpochDay = nowEpochDay - 365*maxYears;
		final long maxEpochDay = nowEpochDay - 365*minYears;
		//See https://stackoverflow.com/questions/34051291/generate-a-random-localdate-with-java-time
	    final long randomEpochDay = ThreadLocalRandom.current().nextLong(minEpochDay, maxEpochDay);
	    return LocalDate.ofEpochDay(randomEpochDay);
	}

	private ResponseEntity<ClientResource[]> _clientsToResources(final List<Client> clients) {
		final Stream<ClientResource> result = clients.stream().map(c -> new ClientResource(c));
		final ClientResource[] resultArray = result.toArray(size -> new ClientResource[size]);
		return new ResponseEntity<>(resultArray, HttpStatus.OK);
	}

    /**Finds the Client for the username, which has been authenticated with this web request.
     * @throws NoClientForUserExc There is no client object with the username of the authenticated user of this web request.
     */
	private Client _findClient(final WebRequest request) {
    	final String username = request.getRemoteUser();
		return _findClient(username);
	}

    /**Finds the Client for the given username.
     * @throws NoClientForUserExc There is no client object with the given username.
     */
	private Client _findClient(final String username) {
		final Optional<Client> optionalClient = bankService.findClient(username);
    	if(!optionalClient.isPresent()) {
    		throw create(NoClientForUserExc.class, username);
    	}
		final Client client = optionalClient.get();
		return client;
	}
    
    /**There is no Client object for the username {0}.*/
    public static class NoClientForUserExc extends Exc{}

    
}
