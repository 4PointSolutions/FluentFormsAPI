# AEM Forms Fluent API

This project provides a fluent API for AEM Forms-based applications.

## Goals of this project

The goal of this project is to improve on that Adobe API in the following ways:

* Use interfaces to improve unit testing.  Interfaces are easier to mock.  For example, Mockito cannot mock the AEM Document object because of its dependencies. 
* Use strong-typing for arguments.  This makes it easier to understand what is required and provides a level of early validation for arguments.  For example, use Path arguments instead of String arguments for filenames.  Invalid Path names will be detected by the Path class and the Path constructors are tailored for file paths.
* Add validations to catch common errors early.  For example, often null arguments return Null Pointer errors from deep in the AEM code with no indications of which argument is the problem.
* Fluent interface to reduce the ceremony around methods that take parameter classes as arguments and make the values being set more obvious to subsequent maintainers.
* Provide builders for parameter classes so that it's easier to create these classes for people who prefer not to use the full fluent interface.
