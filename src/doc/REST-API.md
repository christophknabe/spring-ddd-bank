# Documentation of the REST Endpoints of the Bank Application

| Verb   | Resource URI with Request Params | Effect                                   |
| ------ | -------------------------------- | ---------------------------------------- |
|        |                                  | Operations for Role **bank**             |
| POST   | /bank/pair                       | Creates 2 random clients, sometimes fail after first. Returns a list of all clients. This is useful for populating the database and for checking, if the transaction rollback mechanism works. |
| POST   | /bank/client                     | Create a client from the passed client resource. |
| DELETE | /bank/client/{username}          | Delete the client with the given username. |
| GET    | /bank/client                     | Returns all clients.                     |
| GET    | /bank/client?fromBirth=isoDate   | Returns all clients born at fromBirth or later. |
| GET    | /bank/client?minBalance=double   | Returns all clients with an account with a balance of minBalance or more. |
|        |                                  | Operations for Role **client**           |
| POST   | /client/account                  | Creates a new account for the authenticated client with his userName. The account gets the name, which is passed as request body. |
| POST   | /client/deposit                  | Deposits the given **amount** of money to the account with the given **accountId**. This is executed as the authenticated client with his username. |
| POST   | /client/transfer                 | Transfers the given **amount** of money from the account with the given **sourceAccountId** to the account with the given **destinationAccountId**. Requires, that the current user is the owner of the given source account. |
| POST   | /client/manager                  | Adds the client with the given **username** as an account manager to the account with the given **accountId**. Requires, that the current user is the owner of the given account. |
