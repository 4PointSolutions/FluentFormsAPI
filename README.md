[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# FluentForms API

The FluentForms API project is a set of Java libraries that allow remote access to 
[Adobe Experience Manager Forms](https://experienceleague.adobe.com/docs/experience-manager-65/content/forms/getting-started/introduction-aem-forms.html?lang=en) (a.k.a. AEM Forms) 
functionality. 

In addition to providing remote access to AEM Forms functionality, it also provides a modern, typesafe, and [fluent](https://en.wikipedia.org/wiki/Fluent_interface) facade over the existing [AEM Forms APIs](https://developer.adobe.com/experience-manager/reference-materials/6-5/forms/javadocs/index.html).

It has a layered architecture with four layers. Each layer targets a different 
type of client application. Each layer is built upon the previous layers and requires less 
client code to use than the layers below it.  The top layer is the Spring Boot Starter.


## Sprint Boot Starter

This is the easiest way to use the FluentForms integration with AEM.  The Spring Boot starter 
publishes Spring beans into the
Spring context that can be utilized easily by any Spring Boot application.

### Getting started with the Spring Boot starter

* Retrieve OSGi bundles and install into your AEM server
* Add starter to project using maven
* Add AEM code to your project

There is a [sample web application](spring/fluentforms-sample-web-app) that uses the Spring Boot 
starter library.  There are also documents outlining the [Spring Beans provided by the 
starter library](spring/ConfigurationBeans.md)  and the [configuration properties](spring/ConfigurationProperties.md) that is uses.

## Java Client Library

 This is the targeted at customers with Java applications that are _not_ based on Spring Boot.

### Getting started with the Java Client Library

* Retrieve OSGi bundles and install into your AEM server
* Add client library to project using maven
* Add AEM code to your project


## AEM REST Services

This is targeted at customers with non-Java applications.

### Getting started with the AEM REST Services

* Retrieve OSGi bundles and install into your AEM server
* Make REST calls to the REST services

## Fluent Forms Core Libraries

This is used internally by the AEM Server REST Services but could be utilized by customers 
writing OSGi bundles in Java that will run directly on the AEM server.

### Getting started with the Fluent Forms Core Libraries
* Retrieve OSGi bundles and install into your AEM server
* Add AEM code to your project


# Retrieving the OSGi Bundles

The bundles can either be built from source or retrieved from this project's [GitHub Packages 
Repository](https://github.com/orgs/4PointSolutions/packages?repo_name=FluentFormsAPI).  
