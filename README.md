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

* Echo

Http Basic Security
-------------------

Every REST service is secured when "security" is set to "basic" like http://localhost:8080/sepro/rest/echo/ping?security=basic

The users are managed by UserManagement REST service.

Asynchronous REST Calls
-----------------------

Asynchronous Job Service is enable. For any asynchronous REST request set "asynch" parameter to "true" like http://localhost:8080/sepro/rest/echo/ping?asynch=true

Http response with "202 Accepted" status code is returned with the location of result (e.g. http://localhost:8080/sepro/rest/asynch/jobs/1403270895037-4).

