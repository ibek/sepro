Service Provider
================

Service Provider is a Java EE application of various services (REST, WS, JMS, EJB, ...) for remote client testing.

Available REST Services (JAX-RS)
-----------------------

url: http://host:port/sepro/rest/{service}

* AcronymsDictionary - /acronym
* Echo - /echo
* HttpStatus - /status
* Timeout - /timout
* UserManagement - /user

Available Web Services (JAX-WS)
----------------------

url: http://host:port/sepro/{service}?wsdl

* AcronymsDictionary - /AcronymsDictionaryService
* Echo - /EchoService
* HttpStatus - /HttpStatusService
* Timeout - /TimeoutService
* UserManagement - /UserManagementService

Available JMS Services
----------------------

* Echo
 * request > queue/Echo
 * response < topic/EchoResponse
* AcronymsDictionary
 * request > queue/AcronymsDictionary
 * response < topic/AcronymsDictionaryResponse

Http Basic Security
-------------------

Every REST service is secured when "security" is set to "basic" like http://localhost:8080/sepro/rest/echo/ping?security=basic

The users are managed by UserManagement WS and REST service.

To perform a secured Web Service call, it is enough to fill basic security authentication details in the HTTP request header.

Asynchronous REST Calls
-----------------------

Asynchronous Job Service is enable. For any asynchronous REST request set "asynch" parameter to "true" like http://localhost:8080/sepro/rest/echo/ping?asynch=true

Http response with "202 Accepted" status code is returned with the location of result (e.g. http://localhost:8080/sepro/rest/asynch/jobs/1403270895037-4).

Asynchronous Web Services
-------------------------

* AsyncEcho - /AsyncEchoService

Asynchronous web services require WS-Addressing SOAP header to determine where to send the response.

WS-Security
-----------

All the web services support WS-Security, so when a SOAP message contains a security SOAP header, the user is authenticated according to the credentials. For a password the both types can be used; PasswordText(plaintext), and PasswordDigest(hashed password Base64(SHA-1(nonce + created + password))).

Test Clients
------------

Module "test" contains the test clients. For example to send a JMS message to a specific queue and location.

