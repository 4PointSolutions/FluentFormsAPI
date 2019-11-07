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

## API Implementation Status

The AEM Forms API consists of a series of smaller APIs that are targetted at specific functions.  The plan is to eventually support all the entire AEM Forms API.  This section outlines the progress towards that goal. 

| AEM Forms API | Fluent API Implemented | Servlet Implemented | Client side adapter implemented |
| ------------- | ---------------------- | ------------------- | ------------------------------- |
| Forms | Yes | In Progress | No |
| Output | No | No | No |
| Document Assurance | No | No | No |
| PDF Generator | No | No | No |
| Assembler | No | No | No |
| Barcoded Forms | No | No | No |
| Convert PDF | No | No | No |
| Send To Printer | No | No | No |

