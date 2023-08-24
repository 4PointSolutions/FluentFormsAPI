# Adaptive Form Submission Handling in fluent-forms-spring-boot-starter

The submission handling code looks for one or more beans in the Spring Context that provide the 
`AfSubmissionHandler`[^1] interface.  This interface has two methods `boolean canHandle(String formName)`
 and `SubmitResponse processSubmission(Submission submission)`.  Whenever a submission comes in, the 
 `canHandler` method is called on each `AfSubmissionHandler` bean � passing in the Adaptive Form�s name. 
The first `AfSubmissionHandler` that returns `true` (to indicate it can handle this form�s submission) 
will have its `processSubmission()` method called.

The `processSubmission()` method receives a `Submission`[^2] object (which is populated with the submission�s information such as the form data, form name, http headers and the submission redirect URL).  The 
`processSubmission()` method returns a `SubmitResponse`[^3] interface which contains information that will be 
placed in the response.  It can currently be a `Response` object, a `SeeOther` object or a `Redirect` 
object (which all implement the `SubmitResponse` interface).  A `Response` object is for returning text, 
html, json, etc. to the browser.  A `SeeOther` will redirect the browser to perform a GET on another URL.  A `Redirect` will redirect the browser to perform a POST to another URL.

There are a bunch of static convenience methods in `AfSubmissionHandler` that means that you probably 
won't have to create an `AfSubmissionhandler` directly, but can use a convenience method like 
`AfSubmissionHandler. canHandleFormNameEquals(String formName, Function<Submission, SubmitResponse> handlerLogic)` 
and others.

There are also static convenience functions in the `SubmitResponse.Response` object that make it easy to 
create the most common responses (e.g. `Response json(String json)`).

[^1]: com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmissionHandler
[^2]: com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmissionHandler.Submission,
[^3]: com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmissionHandler.SubmitResponse
