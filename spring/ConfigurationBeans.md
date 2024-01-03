# Configuration Beans

## Introduction

This page documents the configuration Spring Beans that can be used to configure the behaviour of the FluentForms Spring 
Boot Starter as well as Spring Beans that the FluentForms Spring Boot Starter inserts into the Spring context for retrieval 
by the client application.

### Beans Optionally Provided by Client Code

#### Adaptive Forms

`AfSubmissionHandler` - By providing one or more `AfSubmissionHandler` beans, the client application can process 
submissions from Adaptive Forms.  See the AdaptiveFormSubmissionHandling document for details.  If no 
`AfSubmissionHandler` beans are provided by the client application, then submissions are forwarded on to AEM for 
processing by the AEM server.

`Function<InputStream, InputStream> afInputStreamFilter` - By supplying `Function<InputStream, InputStream>` 
with a name of `afInputStreamFilter`, you can override the default stream filter provided by the Spring Starter.  The
default bean provided by the Spring Starter looks like this:
```java
	public Function<InputStream, InputStream> afInputStreamFilter(AemProxyConfiguration aemProxyConfig) {
		FormsFeederUrlFilterBuilder builder = StandardFormsFeederUrlFilters.getUrlFilterBuilder();
		builder = aemPrefix.isBlank() ? builder : builder.aemPrefix(aemPrefix);
		builder = clientPrefix.isBlank() ? builder : builder.clientPrefix(clientPrefix);;
		return builder.buildInputStreamFn();
	}
```  
If you supply your own afInputStreamFilter bean, you should use the code above as a starting point to add additional 
replacement strings.

`AfSubmitProcessor` - The submission processing code looks for a bean of this type.  The Fluent Forms Spring Boot 
starter provides reasonable default implementations based on whether they an `AfSubmissionHandler` has been provided 
by the client application or not. Only in rare cases would a client application need to provide one of these beans.

`ResourceConfigCustomizer` - The Fluent Forms Spring Boot Starter uses the Jersey Spring Boot Starter for providing 
the REST endpoints used to reverse proxy AEM Adaptive Forms and their submissions.  It provides a default 
`ResourceConfigCustomizer` implementation however a client application can override the default if it is required. 
Only in rare cases would a client application need to provide one of these beans.

#### AEM Configuration

`DocumentFactory documentFactory` - By providing a `DocumentFactory` called `documentFactory`, you can provide a 
factory class that will be called whenever a new `Document` object is created by the Fluent Forms code.  By default 
one that uses a simple in-memory implementation for the `Document` object is provided.

### Beans Provided by the Fluent Forms Spring Boot Starter

`AdaptiveFormsService` - Calls AEM to render an Adaptive Form with or without pre-population data.

`AssemblerService` - Calls AEM to invoke the AEM AssemblerService API.  This service is used to manipulate PDFs.

`DocAssuranceService` - Calls AEM to invoke the AEM DocAssuranceService API.  This service is used to Reader Enable 
interactive PDFs.

`FormsService` - Calls AEM to invoke the AEM FormsService API.  This service is used to render interactive PDFs. 

`GeneratePDFService` - Calls AEM to invoke the AEM PDF Generator API.  This service is used to convert Office 
documents to PDF format.

`Html5FormsService` - Calls AEM to render an HTML5 Form with or without pre-population data.

`OutputService` - Calls AEM to invoke the AEM OutputService API.  This service is used to render non-interactive PDFs.

`PdfUtilityService` - Calls AEM to invoke the AEM PdfUtilityService API.  This service is used to perform various 
actions on a PDF such as converting a PDF to XDP, cloning, redacting, sanitizing and extracting the PDF's properties.

