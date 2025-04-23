# Configuration Properties

## Introduction

This page documents the configuration properties that can be used to configure the behaviour of the FluentForms Spring Boot Starter.

### AEM Configuration Properties

These configuration properties are used when connecting to AEM.

`fluentforms.aem.servername` - This property is the machine name of the server where AEM is running.  It is used by FluentForms 
when connecting to AEM.

`fluentforms.aem.port` - This property is the port that AEM is running on.  This is usually 4502 (or 4503 for a publish instance).

`fluentforms.aem.user` - This is the AEM user that be used to log into AEM when communicating with AEM.  In dev and test 
scenarios, admin can be used however it is recommended that a different user (one that is created for the sole purpose of
authenticating FluentForms calls) be used in production.  The created user does not normally need any additional permissions 
beyond being a member of the `forms-users` group.

`fluentforms.aem.password` - This is the password that will be used to log into AEM when communicating with AEM.  The [Jasypt spring boot starter library](https://github.com/ulisesbocchio/jasypt-spring-boot) is a dependency of the FluentForms Spring Boot 
starter.  The password property (well any of the Fluent Forms properties, really) can be encrypted with the Jasypt (see the
spring boot starter web site for more details.

`fluentforms.aem.useSsl` - This is used to indicate whether FluentForms should connect to AEM using https or http.  This property 
can be `true` or `false`.  `true` will tell FLuentForms to log in with https, `false` will result in an http connection.  This 
property is optional, and it defaults to `false` if it is not supplied.

`fluentforms.aem.sslBundle` - This is the name used to locate a Spring Security SSL Bundle that will be used as a trust store 
for SSL HTTPS connections to AEM.  This property is optional, and it defaults to `aem` if it is not supplied.

### Adaptive Forms

`fluentforms.rproxy.enabled` - This is used to enable/disable the reverse proxying of secondary resources to AEM.  If it is 
`true`, then reverse proxying is enabled otherwise it is not.  This property is optional and defaults to `true`.

`fluentforms.rproxy.aemPrefix` - This is used if the AEM instance is running under some directory other than the root 
directory on the AEM server.  This can happen, for instance, when AEM is running under a JEE Application server.

`fluentforms.rproxy.clientPrefix` - This is used if an application using the FluentForms reverse proxy functionality is located 
somewhere besides the root of the web server.  Normally, all secondary resources (like js and css files) are routed through `/aem`, however if the application is deployed to a app server under `/clientApp`, then the reverse proxy will reside under `/clientApp/aem`.  In order to compensate for this, the configuration property `fluentforms.rproxy.clientPrefix` should 
be set to `/clientApp` (as in `fluentforms.rproxy.clientPrefix=/clientApp`).

`fluentforms.rproxy.afBaseLocation` - TBD - This needs to be documented.

### Rest Client Properties

Fluent Forms can be configured to use one of two different REST client libraries.  It can use either the Jersey client libraries or the Spring RestClient libraries.  it was originally developed to use the Jersey client libraries however this some drawbacks - firstly, the Spring requires special configuration when you combine it with libraries that use  the built in Spring Web Mechanisms (e.g. Spring MVC, Spring Boot Activator, etc. - see [https://docs.spring.io/spring-boot/how-to/jersey.html](https://docs.spring.io/spring-boot/how-to/jersey.html)). Secondly, Spring Jersey is an extra dependency that duplicates functionality that is already available in the Spring Framework, so it causes some jar bloat.

The intention is to eventually eliminate Jersey as a required dependency (and make it an optional dependency).

A FluentForms application can choose which library it wishes to use based on a configuration value.

`fluentforms.restclient` - This is used to specify which REST client library will be used to make REST calls to AEM.  It can have one of the following values: `springrestclient` - To use the Spring Framework REST client, or `jersey` to use the Spring Jersey libraries.  If omitted, the application setting defaults to `jersey` for backwards compatibility however this is likely to change at some point in the future to use the `springrestclient` by default.. 

## Encrypting Configuration Property Values (such as passwords)

Some of the settings in the properties files contains usernames and passwords that, for security reasons, should not be stored in plain text. These can be encrypted using Jasypt and stored in the application.properties or profile properties files surrounded by ENC() to denote an encrypted property. The application will automatically decode these encrypted passwords if the encrypted properties are enabled.

The fluentforms Spring Boot starter uses a [jasypt-spring-boot-starter](https://github.com/ulisesbocchio/jasypt-spring-boot). To enable encrypted properties, create properties in the `src/main/resources` directory of your application project and add the following properties:
```
jasypt.encryptor.algorithm=PBEWITHHMACSHA512ANDAES_256
jasypt.encryptor.password=4Point
jasypt.encryptor.iv-generator-classname=org.jasypt.iv.RandomIvGenerator
jasypt.encryptor.salt-generator-classname=org.jasypt.salt.RandomSaltGenerator
```

To encode usernames or passwords, perform the following steps:

1. Download the latest jasypt distribution release from [https://github.com/jasypt/jasypt/releases](https://github.com/jasypt/jasypt/releases) (1.9.3 at the time of this writing)
2. Open the .zip and extract the directory to root (so creating `C:\jasypt-1.9.3`)
3. CD into the new directory (i.e. `cd C:\jasypt-1.9.3`)
4. Run the following command: `bin\encrypt.bat "password=4Point" "algorithm=PBEWITHHMACSHA512ANDAES_256" "saltGeneratorClassName=org.jasypt.salt.RandomSaltGenerator" "ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator" "input=<username or password>"` where _\<username or password\>_ is the string you wish to encrypt.
5. This will produce some output like this:
```
----ENVIRONMENT-----------------

Runtime: Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 11.0.12+8-LTS-237

----ARGUMENTS-------------------

input: testPassword

password: 4Point

saltGeneratorClassName: org.jasypt.salt.RandomSaltGenerator

ivGeneratorClassName: org.jasypt.iv.RandomIvGenerator

algorithm: PBEWITHHMACSHA512ANDAES_256

----OUTPUT----------------------

ZvyYeP694ZXtlp7VfjziAiayVLrnV5NiSqB4fdhDn9DZw6OMWMcN5CHBB4tCQFo+
```
6. Place the encoded string in the correct property within the .properties file surrounded by ENC(). For example:`fluentforms.aem.password=ENC(ZvyYeP694ZXtlp7VfjziAiayVLrnV5NiSqB4fdhDn9DZw6OMWMcN5CHBB4tCQFo+)`
7. Restart the application
