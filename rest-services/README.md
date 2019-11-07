[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# AEM Forms REST Services

This project provides REST services for the AEM Forms Document Services APIs.

## Goals of this project

The goal of this project is to provide REST services that an application can call in order to execute AEM Form functions.

## API Implementation Status

The AEM Forms API consists of a series of smaller APIs that are targeted at specific functions.  The plan is to eventually support all the entire AEM Forms API.  This section outlines the progress towards that goal. 

| AEM Forms API | REST Service Implemented |
| ------------- | ---------------------- |
| Forms | Partial |
| Output | No |
| Document Assurance |
| PDF Generator | No |
| Assembler | No |
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

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html
