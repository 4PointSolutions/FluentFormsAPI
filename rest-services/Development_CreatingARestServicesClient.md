#Steps in creating a REST services client

The REST Services client code relies on the FluentForms library for a lot of its functionality.  This document assumes that whatever service is
being implemented already has a corresponding FluentForms interface implemented in that library.

There are three parts to any implementation of a service:
* `rest-services.server` - This is the server side implementation.  It consists of a Sling servlet per service method.  That servlet accepts 
    parameters that represent the method's arguments and turns those parameters into the corresponding Java object before calling the service
    method.  It returns the results of calling the service as a response to the servlet call.
* `rest-services.client` - This is the client side of the implementation.  It turns calls to FluentForms service methods into a REST call to `rest-services.server` servlets.  It converts service arguments to servlet parameters before calling the servlet.  It turns the response from
    the servlet into Java objects that are returned from the method call.
* `rest-services.it.test` - This is a set of integration tests that make use of a running AEM server that has `fluentforms` and `rest-services.server`
    bundles installed on them.  There are test for both the `rest-services.server` code and the `rest-services.client`/`rest-services.client` code operating together.

The steps involved in implementing a new service in the REST Services codebase is as follows:

### 1. Create the servlet code in the rest-services.server project
1. Create a new package for this service (with the same name the service uses in fluentforms)
1. Create a servlet for each method in the service.  The servlet accepts the method's parameters and makes a call to AEM via the fluentforms library.  it
     returns the result from AEM in the response.

### 2. Create the ServiceAdapter in the rest-services.client project
1. Create a new package for this service (with the same name the service uses in fluentforms)
1. Create a `RestServicesWhateverServiceAdapter` class that implements `TraditionalWhateverService` interface.  This piece used JAX-RS HTTP client
     to create a multipart/forms-data POST to the servlet created in step 1.

### 3. Create integration tests in the rest-services.it.tests project
1. Create two new packages for this service (with the same name the service uses in fluentforms) under the src/test/java.
     One package should be under client.whatever and the other under server.whatever
1. Create tests under `server.whatever` that test the service on the AEM server by calling the servlet directly.  This ensures that a client
     does not need to necessarily use the client libraries but can initiate the REST calls directly from their code if they wish.
1. Create tests under client.whatever that test the service on the AEM server by calling the servlet via the `rest-services.client` library.  This
     ensures that the client/server/aem combination works as expected.

This guide does not specifically talk about the creation of unit tests, but this is assumed to be part of "being done".  When the guide discusses creation of classes, the 
creation of unit tests that test those classes is assumed.
 