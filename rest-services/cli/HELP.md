# AEM Command Line Client

This application can be used to invoke AEM functionality from the command line.

It is a thin wrapper around the AEM APIs so, as such, it inherits some of the quirks of the APIs.  These include:

1. Since each API is different, there is very little consistency in the command line arguments across different APIs
because the APIs themselves have very little consistency.

1. The output from an API could take several forms (a PDF, json, xml, etc.).  The actual output depends on the API method and the command arguments provided.

1. It helps to look at the API documentation to determine what the various command line arguments do.

# Getting Started

## General Format of commands

## Common parameters

| --cred username:password | Username and password (only basic authentication is supported) |
| --aem url | An url in the form "http://localhost:4502/" or "https://aem.example.com/".  The application parses this to get information such as the machine name, the port and whether to use https. |
| --method methodName | The name of the method to call.  There will be method-specific parameters. 

### Reference Documentation
For further reference, please consider the following sections:

* [Official AEM Java API documentation](https://helpx.adobe.com/experience-manager/6-5/forms/javadocs/index.html) 

