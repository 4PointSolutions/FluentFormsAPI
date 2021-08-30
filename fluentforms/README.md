[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# AEM Forms Fluent API

This project provides a fluent API for AEM Forms-based applications.

## Goals of this project

The goal of this project is to improve on that Adobe API in the following ways:

* Use interfaces to improve unit testing.  Interfaces are easier to mock.  For example, Mockito cannot mock the AEM Document object because of its dependencies. 
* Use strong-typing for arguments.  This makes it easier to understand what is required and provides a level of early validation for arguments.  For example, use Path arguments instead of String arguments for filenames.  Invalid Path names will be detected by the Path class and the Path constructors are tailored for file paths.
* Add validations to catch common errors early.  For example, often null arguments return Null Pointer errors from deep in the AEM code with no indications of which argument is the problem.
* Fluent interface to reduce the ceremony around methods that take parameter classes as arguments and make the values being set more obvious to subsequent maintainers.
* Provide builders for parameter classes so that it's easier to create these classes for people who prefer not to use the full fluent interface.

## Getting Started

Here are the steps to get started:
1. Build a version of the project using the Maven goals outlined in the fluentforms/pom.xml file.
1. Install the bundle on an AEM instance (using autoInstallBundle or by copying a copy of the fluentforms.core-0.0.1-SNAPSHOT.jar file into the AEM crx-quickstart/install directory.
1. Install the bundle into your local maven repository (using the Maven install goal).
1. Create a project for the bundle that will be using fluentforms.core.
1. Add the following dependency to your new bundle project:

```xml
    <dependency>
        <groupId>com._4point.aem</groupId>
        <artifactId>fluentforms.core</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
```
  
There are samples of how to invoke the fluentforms APIs in the examples subproject.  Here is an example using the OutputService:

```java
import org.osgi.service.component.annotations.Reference;
 
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.factory.ServerFactory;
 
class OutputServiceExample {
 
   @Reference
   private com.adobe.fd.output.api.OutputService adobeOutputService;
 
   InputStream demoGeneratePdfOutputTypical() throws Exception {
      OutputService outputService = ServerFactory.createOutputService(adobeOutputService);
 
      // 
      Document data = ServerFactory.getDefaultDocumentFactory().create(sampleData.getBytes());
 
      Document result = outputService.generatePDFOutput()
                                     .setContentRoot(PathOrUrl.fromString("crx:/content/dam/formsanddocuments/"))
                                     .setTaggedPDF(true)
                                     .executeOn(Paths.get("foo/bar.xdp"), data );
 
      // byte[] pdfBytes = result.getInlineData();	// you can do either of these but not both.
      InputStream pdfStream = result.getInputStream();
 
      return pdfStream;
   }
}
```

## Architecture

This project consists of several sub-projects with each subproject representing a layer in the architecture (i.e. each layer depends on the layers beneath it).

Here is an overview of the layers:
* _FluentForms API_  – This is a wrapper around the Adobe Document Services APIs.  It provides a fluent interface (which reduces the code required to use Adobe APIs and improves readability) and additional validations that the Adobe APIs do not.  It is deployed as a bundle on an AEM instance.
* _FluentForms REST Services_  – This is layered on top of the  _FluentForms API_  and it makes the Adobe APIs available as REST service endpoints.  It is deployed as a bundle on the AEM instance as well.
* _FluentForms REST Services client_  – This is a client-side API that wraps calls to the  _FluentForms REST Services_ .  You include this as a dependency in your Java application.  Your Java application then makes API calls and this layer turns them into REST calls to the AEM server running the  _FluentForms REST Services_ .

Client code that utilizes this project can access whatever layer is appropriate for their needs, for example, a .NET shop might write .NET code that makes REST calls to the  _FluentForms REST Services_  layer directly, bypassing the Java client code.  Likewise a user with OSGi experience might choose to leverage just the  _FluentForms API_  in an OSGi Java bundle in order to make the calls to AEM more concise.

## API Implementation Status

The AEM Forms API consists of a series of smaller APIs that are targeted at specific functions.  The plan is to eventually support the entire AEM Forms API.  This section outlines the progress towards that goal.  Each column is part of one of the architectural layers (above).  The first two columns are part of the first layer and each of the other two columns represents components within the last two layers outlined above.

| AEM Forms API | Fluent API Implemented | Servlet Implemented | Client side adapter implemented |
| ------------- | ---------------------- | ------------------- | ------------------------------- |
| Forms | Yes | Yes | Yes |
| Output | Partial | Partial | Partial |
| Document Assurance | Partial | Partial | Partial |
| PDF Generator | Partial | Partial | Partial |
| Assembler | Partial | Partial | Partial |
| Barcoded Forms | No | No | No |
| Convert PDF | No | No | No |
| Send To Printer | No | No | No |

