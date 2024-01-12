The FluentFormsAPI project consists of three interrelated sub-projects.  All 
sub-projects are Java projects that use Maven as a build tool.

# fluentforms
[This project](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/fluentforms) is a 
fluent API wrapper around the Adobe AEM Forms Java APIs.  It provides a bunch of small 
improvements over the Adobe API (stronger typing, stronger validation, enhanced readability 
for starters).

# rest-services
[This project](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/rest-services) is 
an [AEM bundle](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/rest-services/rest-services.server) and [client library](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/rest-services/rest-services.client) that provides a REST services wrapper 
on top of the Adobe AEM Forms Java APIs (via the fluentforms API).  It provides both a 
server-side bundle for receiving 
API calls and a Java client library (.jar) that makes it easy to call the rest-services 
server API.

# fluentforms-spring-boot-starter
[This project](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/spring) contains 
four maven projects.  

There are two projects which provide Spring Boot functionality.  There is a [Spring Boot auto-configuration project](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/spring/fluentforms-spring-boot-autoconfigure) which 
contains code to publish the Fluent Forms AEM services as beans in the Spring context and to 
retrieve configuration properties used to configure those beans.  There is a 
[Spring Boot Starter](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/spring/fluentforms-spring-boot-starter) 
which initiates the auto-configuration when it is included in a Spring Boot project.

There are also two sample projects.  There is a [Spring Boot Sample Web Application](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/spring/fluentforms-sample-web-app) 
and a [Spring Boot Sample CLI Application](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/spring/fluentforms-sample-cli-app) 
which is a work in progress.

