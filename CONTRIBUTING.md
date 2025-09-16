# Contributing to Fluent Forms

Thank you for your interest in contributing to Fluent Forms! 
Contributions to this project are gratefully accepted however we do have to enforce some standards in order to keep the 
amount of work required to maintain this project to a reasonable level.

These contributing standards have been put in place to ensure that contributions don’t increase the technical debt of the project nor disproportionately increase the maintenance effort on the project.  If a pull request does not meet these contributing guidelines, it will not be committed into the main branch of the project.  It will remain as a PR until someone (either the original contributor, a committer or some interested third party) devotes the required time to do the additional required work. If a PR is outstanding for too long, the original work may become out of sync with the main branch and the committers may decide it is no longer relevant.  In this case the PR will be closed without being committed to the main branch of the project.

The main purpose of this project is to act as a shared codebase for 4Point AEM Forms customers however it is available for use
by anyone else under the terms of the Apache 2.0 License. 

The maintainers of this project are full-time employees whose job duties do not necessarily entail maintaining the source code
of this project.  Depending on their current duties and workload, it may take up to 10 working days to respond to project issues, pull requests, etc.  If you haven’t heard anything by then, feel free to ping the thread.

If you're looking for better responsiveness, please consider arranging a paid services engagement with 4Point prior to beginning work on a contribution.  We can help with design advice, mentoring, and code reviews as part of the contribution process.  We can also perform the entire enhancement work as a paid services engagement.  We can be reached at [Sales@4Point.com](mailto:sales@4point.com).

Your contributions help make this project better for everyone.

## How to Contribute:

1. Reporting Issues

	* Search existing issues before opening a new one.  
	* Provide a clear and descriptive title.  
	* Include steps to reproduce, expected behavior, and actual behavior.  
	* Attach logs, stack traces, or screenshots if relevant.  

2. Submitting Pull Requests

	* Before starting any code changes, submit an issue to inform the maintainers. This allows them to provide feedback on your proposal, including preferred approaches and behaviors to avoid.
	* Fork the repository and create a new branch from master, using the naming format _\<issue#>_-_\<short description\>_. This helps identify the branch’s purpose and trace it back to the original issue.
	* Write clear, concise commit messages.  
	* Add or update unit tests as appropriate.  All new classes must have unit tests and that any changes to existing classes will require updating the associated unit tests unless the change is just refactoring existing code with no changes to the classes public API.
	* Unit tests should not require a running AEM instance, therefore performing a build should not require a running AEM instance.  
	* Run all tests locally before submitting.
	* Run a static analysis tool (such as Spot Bugs or SonarQube) before submitting.  It is not necessary to fix minor issues, but major issues should be addressed.
	* Reference related issues in your pull request description.

3. Coding Standards

	* Use Java 21 features as appropriate.  The project typically uses the most recent version of Java that AEM supports (Currently Java 21). 
	* Use Eclipse’s default Java formatting with 4-character tabs, except the maximum line length can be increased from 72 to 144 characters. 
	* Document public classes and methods with Javadoc.
	* The general class structure of new services created should mirror the existing class structure.  See the [Development_CreatingARestServicesClient.md](Development_CreatingARestServicesClient.md) file for details.

4. Project Structure

	*  The FluentFormsAPI project consists of three interrelated sub-projects:
		*  [`fluentforms`](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/fluentforms) is a fluent API wrapper around the Adobe AEM Forms Java APIs.  It provides a bunch of small improvements over the Adobe API (stronger typing, stronger validation, enhanced readability for starters).
		* [`rest-services`](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/rest-services) is an [AEM bundle](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/rest-services/rest-services.server) and [client library](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/rest-services/rest-services.client) that provides a REST services wrapper on top of the Adobe AEM Forms Java APIs (via the fluentforms API).  It provides both a  server-side bundle for receiving API calls and a Java client library (.jar) that makes it easy to call the rest-services server API.
		* [`fluentforms-spring-boot-starter`](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/spring) contains four maven projects:
			*  [Spring Boot auto-configuration project](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/spring/fluentforms-spring-boot-autoconfigure) which 
contains code to publish the Fluent Forms AEM services as beans in the Spring context and to 
retrieve configuration properties used to configure those beans.
			*  [Spring Boot Starter](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/spring/fluentforms-spring-boot-starter) which initiates the auto-configuration when it is included in a Spring Boot project.
			* [Spring Boot Sample Web Application](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/spring/fluentforms-sample-web-app) which is a sample Spring Boot web application that utilizes the Spring Boot starter.
			*  [Spring Boot Sample CLI Application](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/spring/fluentforms-sample-cli-app) which is a sample command line application that utilizes the Spring Boot starter.
	*  All sub-projects are Java projects that use Maven as a build tool.  
	*  All sub-projects use the default Maven project structure  

5. Integration Tests

	* There is a dedicated integration test project: [`rest-services.it.tests`](https://github.com/4PointSolutions/FluentFormsAPI/tree/master/rest-services/it.tests).
	* Both client-side and server-side codebases have integration tests.
	* New code contributions must include integration tests to verify functionality on a real AEM instance.
	* Integration tests should be:
		* Self-contained and independent of the AEM instance whenever possible.
		* The only required precondition: `fluentforms` and `rest-services` bundles must be installed on AEM.
		* _Exceptions to the two previous points exist_:
			* Testing Adaptive Forms requires the form to reside on the AEM instance.
			* Testing CRX functionality may require assets to be deployed into CRX first.
	* In most cases, tests should send all necessary data in a single, self-contained transaction.

6. Code of Conduct

This project adheres to the Contributor Covenant Code of Conduct. By participating, you are expected to uphold this code.

### Getting Help

* For questions, open a discussion or contact the maintainers.

Thank you for helping make Fluent Forms better!
