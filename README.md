[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# FluentFormsAPI
The FluentFormsAPI project will eventually consist of three interrelated sub-projects.  All sub-projects are Java projects that use
Maven as a build tool.

# fluentforms
[This project](tree/master/fluentforms) is a fluent API wrapper around the Adobe AEM Forms Java APIs.  It provides a bunch of small
improvements over the Adobe API (stronger typing, stronger validation, enhanced readability for starters).

# rest-services
[This project](tree/master/rest-services) is an AEM bundle and client library that provides a REST services wrapper 
on top of the Adobe AEM Forms Java APIs (via the fluentforms API).  It provides both a server-side bundle for receiving
API calls and a Java client library (.jar) that makes it easy to call the rest-services server API.

# aemforms-ivs
Eventually, we would like to add a third project which is a server bundle that produces HTML pages that can be used to call the 
rest-services APIs.  This would allow a user to invoke AEM APIs from a browser and would provide equivilent functionality to
the AEM Forms for JEE IVS applications.
