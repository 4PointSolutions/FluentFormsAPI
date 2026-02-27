# Migration Guide

This document outlines changes that may affect users moving between versions.  It is ordered from most recent version to oldest version.

## 0.0.5
#### Bundles
* No external changes

#### Client
* Now requires Spring 4.x
* Refactored out Jersey into separate autoconfiguration
	* Use `fluentforms-jersey-spring-boot-starter` instead of `fluentforms-spring-boot-starter` to continue using Jersey.
* `fluentforms-spring-boot-starter` now uses Spring's RestClient for making calls to AEM.

## 0.0.4
#### Bundles
* Now requires AEM LTS (a.k.a. AEM 6.6)
* Now requires Java 17 or later

#### Client
* Replaced the `generatePrintedOutput()` method with a new `generatePrintedOutput(PrintConfig printConfig)` ([issue #53](https://github.com/4PointSolutions/FluentFormsAPI/issues/53)).  Existing code should be migrated to the new version.
* RestServices client helper `BuilderImpl` now requires a `RestClientFactory` which supplies a `RestClient`.  This is the hook to supply either a Jersey-based `RestClient` implementation or a Spring-based `RestClient` implementation.  Any code that directly instantiates services using the Builder should either supply a factory or (preferably) allow the Spring Boot autoconfiguration to create the services.

## 0.0.3
#### Bundles
* This is the last version that supports AEM 6.5 (non-LTS).
* Requires Java 8

#### Client
* Requires Spring Boot 3.3.5 or later
* Requires Java 17 or later
