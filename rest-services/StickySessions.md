## Abstract
This document discusses why sticky sessions are required for generating prefilled Adaptive Forms using FluentForms.  It discusses the constraints imposed by Adobe's Adaptive Forms API and the reasons why the current implementation works the way it does.
## Background
The current implementation prefills an Adaptive form by making two calls to AEM.  The first call POSTs the data to a custom servlet that stores the data in memory and returns a unique identifier back the client.  The client then makes a second call, this time a GET to the Adobe Adaptive Form rendering URL, passing in the query parameter containing the unique identifier.  AEM then retrieves the data from memory using that identifier (which removes the data from memory) and renders the pre-filled Adaptive Form.

This benefits of this approach are:
* it works reliably
* it uses documented AEM APIs
* it is secure, no data touches disk
* it is fast

Unfortunately, as a consequence of making two calls to AEM and due to the fact that the data is stored in memory, both calls must go to the same AEM instance.  This requires that AEM multi-instance topologies must implement stick sessions which can impede load balancing.

The rest of this paper discusses the constraints imposed by the API and alternative implementations that were rejected.

## Adobe's API
Adobe's Adaptive Forms API is implemented as a REST GET endpoint.  Parameters are passed in via the URL (either as path parameters or as query parameters).  Because the call is an HTTP GET request, no data can be passed in the body of the request.  All data must be passed in (by value or by reference) in a query parameter.

The [Adobe's documentation for prefilling Adaptive Forms](https://experienceleague.adobe.com/en/docs/experience-manager-65-lts/content/forms/adaptive-forms-advanced-authoring/prepopulate-adaptive-form-fields#supported-protocols-for-prefilling-user-data) outlines several approaches:
* Using the dataRef query parameter containing a link that uses one of the approved protocols:
	* `crx://` to point to a location in CRX in the AEM instance's local repository
	* `file://` to point to a location on the AEM instance's local disk
	* `https://` to point to a location served from a web server
	* `service://` to point to a [custom prefill service](https://experienceleague.adobe.com/en/docs/experience-manager-65-lts/content/forms/adaptive-forms-advanced-authoring/prepopulate-adaptive-form-fields#create-and-run-a-prefill-service) running locally on the AEM instance
* Setting a `data` attribute on the `slingRequest` (requires the Adaptive Form to incorporate a custom component that retrieves the data and stores it in the request).

Note that all the protocol approaches require that data reside somewhere awaiting retrieval from AEM when the GET request for the Adaptive Form arrives negating the ability to pass in the data as part of the Adaptive Form request.  

Also note that the majority of the protocols require that the data reside on the local AEM instance somewhere (i.e. crx, file system, etc.).  If the data resides on the client, then these protocols require that the data first be placed locally on the server that will be rendering the adaptive form before the GET is called to render it, thus mandating two calls (and therefore sticky sessions).

The exceptions to this are:
1. `https://` - This can point to a remote server where the data resides, so while this may require two calls if the data comes from the client application, the two calls do not have to go to the same server, thus eliminating the need for sticky sessions.  The server being called, however, cannot require authentication, so this approach is not secure without resorting to implicit forms of authentication such as certificate-based authentication.
2. `service://` - A custom prefill service could retrieve the data from virtually anywhere.  This means that, like the previous approach, there may be two calls required if the data resides on the client however it would not require sticky sessions.  Programming a custom prefill service requires that it reside and run on the AEM platform.  This can present difficulties for non-trivial services as the AEM platform is primarily designed to support running AEM, not custom application code.  It also requires some working knowledge of OSGi.

Setting a `data` attribute on the `slingRequest` has the potential to allow the data to be retrieved from the incoming GET request (assuming the data can be stuffed into something that is part of the Adaptive Form GET request, such as a header).  It however requires that the every form be built to include a custom component that transfers the data from the incoming request into the `data` attribute on the `slingRequest`.

## Current Implementation
The current FluentForms Adaptive Forms pre-fill implementation consists of three parts:
1. A "Data Cache" servlet that accepts a POST request containing data, stores the data into an in-memory hash map with a unique key, and then returns that unique key to the caller.
2. An AEM custom prefill service that, given a key generated by the "Data Cache" servlet, retrieves, removes and returns the data associated with that key from the in-memory hash map.
3. A client side method that, when called to render the Adaptive Form, POSTs the data to the "Data Cache" servlet, get the key back and the then makes a GET call the AEM Adaptive Form rendering endpoint passing in a `dataRef=service:://` query parameter that contains the name of the prefill service and the key it got from the "Data Cache" servlet. 

## Idea for supporting other approaches
We could alter the client code to accept a "dataRef" parameter String.  When a "dataRef" parameter String is provided, the client code would skip the call to the "Data Cache" servlet and instead place the "dataRef" parameter String directly into the `dataRef` query parameter on the GET.  This would allow any of the other protocols to be used.

It would be the responsibility of the caller to ensure that whatever resides at the other end of the protocol would return valid data and that the whole process is secure.