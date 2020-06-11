# Implementing a new API for Fluent Forms

Here are some notes about the steps required in order to implement an AEM Forms API that is not already implemented in Fluent Forms.

### 1. Create new packages for the Service
Each service has three associated packages
* `.api.whatever` - This contains the public interfaces for the service
* `.impl.whatever` - This contains the implementations behind the interfaces
* `.testing.whatever` - This contains the mock classes for unit testing client code without an AEM server.
The parent packages  (`.api`, `.impl`, `.testing`) contain classes/interfaces that are common across multiple services.

### 2. Create a "Traditional" version of the service and the `AdobeWhateverServiceAdapter` class.
This is an interface that looks like the standard Adobe API but replaces most of the Adobe objects with Fluent Forms versions of those objects.
Some examples are replacing Adobe Document objects with Fluent Forms Document interface (so that Documents can be mocked and have other implementations),
or Options objects that have Builder implementations, additional argument validation and strong argument typing.  Some Adobe objects are not replaced, for example, enums are not typically replaced as the code would just duplicate Adobe functionality without providing any value.

The `AdobeWhateverServiceAdapter` is an implementation class that maps FluentForms objects to Adobe objects and calls the Adobe APIs.  It is the primary
class that uses Adobe objects.  Outside of this class, the use of Adobe objects should be limited to enums and static classes that contain constants.

The way I typically do this is to:
* I create a new `AdobeWhateverServiceAdapter` Java class that extend/implements the original Adobe class/interface.
* I then use my IDE to override/implement all 
the Adobe methods.
* I then remove the extend/implement keywords and remove all the Adobe imports.
* I then go through and import all the appropriate Fluent Forms classes. 
* I may need to perform the extend/implement approach on other classes in order to complete the `AdobeWhateverService` implementation.
* Each parameter object (a parameter object is one used to pass options into a method - their names usually end in "Options") that I create implements a similarly named interface (as we try and make sure that, other than the top level implementation, clients only ever program against interfaces).  This allows for the following additional changes to be made to the Adobe parameter objects.
    * Replace dependencies on `com.adobe.aemfd.docmanager.Document` with `com._4point.aem.fluentforms.api`
    * Implement "Setter" interfaces that keep the object and the corresponding “fluent builder” object synchronized
    * Make improvements to the interface.  This includes things like stronger typing and adding null checking into the code.
* Once the `AdobeWhateverService` is implemented, you can Extract the `TraditionalWhateverService` interface from that Adapter class.

### 3.  Create a `SafeWhateverServiceAdapterWrapper`.
This layer implements the Traditional interface and wraps the Adapter class.  It contains validations to make sure that no null arguments are passes to the Adobe layer.
It may perform other checks as well.  The goal of this class is to ensure that invalidate data is caught early rather than being passed to Adobe.

### 4.  Create Fluent APIs for each method.
Now that the wrappers around the original non-fluent Adobe interface has been created, we can start implementing the fluent APIs.

For each method in the interface,
we implement an argument builder object that lets you build up the arguments for each method in the service.  These are inner classes within the implementation class
that implement `set()` methods for each parameter and an `executeOn()` method that takes mandatory arguments.  We use method overloading to support multiple types
for a particular argument (rather than using loose typing as the Adobe interfaces typically do).  The corresponding interface is placed as an inner class within the main
service interface.

Each `ArgumentBuilder` object implements a `Setter` interface that it shares with the implementation of that object.  This ensures that the Builder and object itself stay in sync.
The builder object also implements the `Transformable` interface so that operations (like a conditional set of an argument) can be implemented using lambdas.

### 5. Create the Mock objects used for client testing
This typically consists of 3 objects per service
* `MockTraditionalService` - This implements the TraditionalService interface and just captures the arguments passed in for later retrieval by the unit testing code.  It typically contains an inner "arguments" object where the arguments for that method are stored and a getter for that object to retrieve it after the mock service is called.
* `ExceptionalMockTraditionalService` - Instead of capturing arguments this always throws an exception so that a client can test error handling.
* `MockService` - This extends the implementation object but constructs the implementation using the MockTraditionalService (or the ExceptionalMockTraditionalService).
It has additional methods for setting the results of the method calls and for retrieving the arguments passed into method calls.  It also has some static methods for constructing the MockService.


This guide does not specifically talk about the creation of unit tests, but this is assumed to be part of "being done".  When the guide discusses creation of classes, the 
creation of unit tests that test those classes is assumed.


