OVERVIEW
========

The FluentForms client library (rest-services.client-*version*.jar) uses standard REST calls to invoke AEM 
functionality.  They are standard HTTP POST calls that use multipart/form-data to invoke the server-side APIs. 
These calls can be emulated by other means (such as HTML forms or any number of other utilities the emulate HTML 
forms).  cUrl is a standard utility for doing just that.

This directory contains documentation on how to use cUrl to invoke FluentForms Rest Services.  While the 
documentation is specific to cUrl.  Anyone who can read the cUrl documentation should be able to translate 
the cUrl parameters into similar parameters for other utilities that also can do HTTP POSTs of multipart/form-data.

Before executing the cUrl calls, the two bundles (fluentforms.core-*version*.jar and 
rest-services.server-*version*.jar) must be installed into the AEM installation (usually by copying 
them into the quickstart/install directory).  These bundles can be downloaded from the GitHub package 
repository using Maven or using the [GrabBundles.java JBang script](../jbang_scripts/GrabBundles.java).

DOCS
====

[RenderPdfForm](./RenderPdfForm.md) - Render an interactive PDF form using the AEM Forms Service.

[GeneratePdfOutput](./GeneratePdfOutput.md) - Render a non-interactive PDF form using the AEM Output Service.

[GeneratePrintedOutput](./GeneratePrintedOutput.md) - Render a printed form using the AEM Output Service.
