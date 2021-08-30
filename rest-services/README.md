[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# AEM Forms REST Services

This project provides REST services for the AEM Forms Document Services APIs.  It allows a client system to make calls to a remote AEM instance in order to perform AEM functions and return the results to the client.  The library handles the task of making REST calls from the client to the AEM server, receiving the REST calls on the server, translating them to AEM API calls and then returning the results to the client.

## Goals of this project

The goals of this project are:

* to provide a REST services interface that any application can call in order to execute AEM Forms functions
* to provide a client library to allow a Java client application to call the REST services interface remotely without having to know REST.

The rest-services code relies on the FluentForms API to wrap the AEM interfaces and provide a fluent interface to clients that are leveraging the rest-services library’s functionality.


## API Implementation Status

The AEM Forms API consists of a series of smaller APIs that are targeted at specific functions.  The plan is to eventually support all the entire AEM Forms API.  This section outlines the progress towards that goal. 

| AEM Forms API | REST Service Implemented |
| ------------- | ---------------------- |
| Forms | Mostly |
| Output | In progress |
| Document Assurance | Partial |
| PDF Generator | Partial |
| Assembler | Mostly |
| Barcoded Forms | No |
| Convert PDF | No |
| Send To Printer | No |


## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallPackagePublish
    
Or alternatively

    mvn clean install -PautoInstallPackage -Daem.port=4503

Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle

## Maven settings   

The project comes with the adobe-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html
