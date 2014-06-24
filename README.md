Service Provider
================

Service Provider is a Java EE application of various services (REST, WS, JMS, EJB, ...) for remote client testing.

Available REST Services (JAX-RS)
-----------------------

* AcronymsDictionary
* Echo
* HttpStatus
* Timeout
* UserManagement

Available Web Services (JAX-WS)
----------------------

* AcronymsDictionary
* Echo
* HttpStatus
* Timeout
* UserManagement

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

* AsyncEcho

Asynchronous web services require WS-Addressing SOAP header to determine where to send the response.

