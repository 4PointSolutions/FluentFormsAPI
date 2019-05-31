# TODO List

This file contains a list of things that I would like to do to improve on the existing API or to address technical debt

## Immediate Items
1. RenderPdfForm server tests (in rest_services.it-tests.server.forms and rest_services.it-tests.client.forms) are not complete.  Currently sending the submitUrl and/or the Cache Strategy cause an internal error response.  These errors should be located and corrected.

1. Setting the XCI value in RenderPdfForm is not currently supported.  This needs to be added.

3. Improve tests in RestServicesFormsServiceAdapterTest.
   1. Add more importData tests for exceptional case (i.e. those cases where exceptions are thrown.
   2. Improve mocking based on [https://maciejwalkowiak.com/mocking-fluent-interfaces/](https://maciejwalkowiak.com/mocking-fluent-interfaces/) 

1. Add support for a Correlation ID header field (write it out to the log).  This has already been added in the client code, but needs to be added to the server code.

## Longer Term Items
1. Having an automated test of the PDFs returned from the integration tests would be nice.  This is probably a lot of work however, so it is on the back burner.

