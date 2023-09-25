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

### Adaptive Forms

`fluentforms.rproxy.enabled` - This is used to enable/disable the reverse proxying of secondary resources to AEM.  If it is 
`true`, then reverse proxying is enabled otherwise it is not.  This property is optional and defaults to `true`.

`fluentforms.rproxy.aemPrefix` - This is used if the AEM instance is running under some directory other than the root 
directory on the AEM server.  This can happen, for instance, when AEM is running under a JEE Application server.

`fluentforms.rproxy.afBaseLocation` - TBD - This needs to be documented.
