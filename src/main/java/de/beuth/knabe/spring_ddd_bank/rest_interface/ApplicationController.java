package de.beuth.knabe.spring_ddd_bank.rest_interface;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.beuth.knabe.spring_ddd_bank.domain.Account;
import de.beuth.knabe.spring_ddd_bank.domain.AccountAccess;
import de.beuth.knabe.spring_ddd_bank.domain.Amount;
import de.beuth.knabe.spring_ddd_bank.domain.BankService;
import de.beuth.knabe.spring_ddd_bank.domain.Client;
import multex.Exc;

//Make the controller transactional according to answer of Rogério at https://stackoverflow.com/questions/23118789/why-we-shouldnt-make-a-spring-mvc-controller-transactional
@Transactional @RestController
public class ApplicationController {

	private final BankService bankService;
	
    @Autowired
    public ApplicationController(final BankService bankService) {
    	this.bankService = bankService;
	}
    
    /*A good resource for the design of REST URIs is https://blog.mwaysolutions.com/2014/06/05/10-best-practices-for-better-restful-api/ */
	
    @PostMapping("/client")
    public ResponseEntity<ClientResource> createClient(@RequestBody  final ClientResource clientResource) {
    	System.out.printf("ApplicationController POST /client %s\n", clientResource);
    	final LocalDate birthLocalDate = LocalDate.parse(clientResource.birthDate, Util.MEDIUM_DATE_FORMATTER);
		final Client client = bankService.createClient(clientResource.name, birthLocalDate);
        return new ResponseEntity<>(new ClientResource(client), HttpStatus.CREATED);
    }

    @DeleteMapping("/client/{clientId}")
    public ResponseEntity<String> deleteClient(@PathVariable  final Long clientId) {
    	System.out.printf("ApplicationController DELETE /client/%s\n", clientId);
    	final Client client = bankService.findClient(clientId).get();
    	bankService.deleteClient(client);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }	
    
    @GetMapping(path="/client")
    public ResponseEntity<ClientResource[]> findClients(
    		@RequestParam(name="fromBirth", defaultValue="") final String fromBirth,
    		@RequestParam(name="minBalance", defaultValue="") final String minBalance
    		) {
    	System.out.printf("ApplicationController GET /client fromBirth=%s, minBalance=%s\n",fromBirth , minBalance);
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

    @PostMapping("/client/{clientId}/account")
    public ResponseEntity<AccountAccessResource> createAccount(@PathVariable  final Long clientId, @RequestBody  final String accountName) {
    	System.out.printf("ApplicationController POST /client/%s/account accountName=%s\n", clientId, accountName);
    	final Client client = bankService.findClient(clientId).get();
    	final AccountAccess r = client.createAccount(accountName);
		final AccountAccessResource result = new AccountAccessResource(r);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /*Resource for a coarse grained business process according to https://www.thoughtworks.com/de/insights/blog/rest-api-design-resource-modeling*/
    @PostMapping("/client/{clientId}/deposit")
    public ResponseEntity<Void> deposit(@PathVariable  final Long clientId, @RequestBody  final DepositCommand command) {
    	System.out.printf(
    			"ApplicationController POST /client/%s/deposit %s\n", clientId, command);
    	final Client client = bankService.findClient(clientId).get();
    	final Account destinationAccount = client.findAccount(command.destinationAccountId).get();
    	final Amount amount = new Amount(command.amount);
    	client.deposit(destinationAccount, amount);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*Resource for a coarse grained business process according to https://www.thoughtworks.com/de/insights/blog/rest-api-design-resource-modeling*/
    @PostMapping("/client/{clientId}/transfer")
    public ResponseEntity<AccountResource> transfer(@PathVariable  final Long clientId, @RequestBody  final TransferCommand command) {
    	System.out.printf(
    			"ApplicationController POST /client/%s/transfer %s\n", clientId, command);
    	final Client client = bankService.findClient(clientId).get();
    	final Account sourceAccount = client.findAccount(command.sourceAccountId).get();
    	final Account destinationAccount = client.findAccount(command.destinationAccountId).get();
    	final Amount amount = new Amount(command.amount);
    	client.transfer(sourceAccount, destinationAccount, amount);
    	final AccountResource result = new AccountResource(sourceAccount);
        return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
    }    

    /*Resource for a coarse grained business process according to https://www.thoughtworks.com/de/insights/blog/rest-api-design-resource-modeling*/
    @PostMapping("/client/{clientId}/manager")
    public ResponseEntity<AccountAccessResource> addAccountManager(@PathVariable  final Long clientId, @RequestBody  final AddAccountManagerCommand command) {
    	System.out.printf(
    			"ApplicationController POST /client/%s/manager %s\n", clientId, command);
    	final Client client = bankService.findClient(clientId).get();
    	final Account account = client.findAccount(command.accountId).get();
    	final Client manager = bankService.findClient(command.managerClientId).get();
    	final AccountAccessResource result = new AccountAccessResource(client.addAccountManager(account, manager));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    } 

    @GetMapping("/client/{clientId}/account")
    public ResponseEntity<String> accountsReport(@PathVariable  final Long clientId) {
    	System.out.printf("ApplicationController GET /client/%d/account\n", clientId);
    	final Client client = bankService.findClient(clientId).get();
    	final String result = client.accountsReport();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /*Example of a transaction, which stores two objects of type Client and sometimes fails.*/
    @PostMapping("/client/test")
    public ResponseEntity<ClientResource[]> create2Clients() {
    	System.out.printf(
    			"ApplicationController POST /client/test\n");
    	final long now = System.currentTimeMillis();
    	final long number = now % 10;
    	final Client client1 = bankService.createClient("Manfred_"+number, LocalDate.ofEpochDay(1000));
    	System.out.printf("Client %s created.\n", client1);
    	if(number%3 == 0) {
    		throw new Exc("Exception after creating {0}. Should have been rolled back.", client1);
    	}
    	final Client client2 = bankService.createClient("Sabina_"+number, LocalDate.ofEpochDay(2000));
    	System.out.printf("Client %s created.\n", client2);
    	final List<Client> clients = bankService.findAllClients();
        return _clientsToResources(clients);
    } 
    

	private ResponseEntity<ClientResource[]> _clientsToResources(final List<Client> clients) {
		final Stream<ClientResource> result = clients.stream().map(c -> new ClientResource(c));
		final ClientResource[] resultArray = result.toArray(size -> new ClientResource[size]);
		return new ResponseEntity<>(resultArray, HttpStatus.OK);
	}
    
}
