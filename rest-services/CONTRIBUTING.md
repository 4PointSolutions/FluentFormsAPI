# Contributing To This Project

Contributions to this project are gratefully accepted however we do have to enforce some standards in order to keep the 
amount of work required to maintain this project to a reasonable level.

The main purpose of this project is to act as a shared codebase for 4Point AEM Forms customers however it is available for use
by anyone else under the terms of the Apache 2.0 License. 

The maintainers of this project are full-time employees whose job duties do not necessarily entail maintaining the source code
of this project.  Depending on their current duties and workload, it may take up to 10 working days to respond to project issues, pull requests, etc.  If you haven’t heard anything by then, feel free to ping the thread.

If you're looking for better responsiveness, please consider arranging a paid services engagement with 4Point prior to beginning work on a contribution.  We can help with design advice, mentoring, and code reviews as part of the contribution process.  We can also perform the entire enhancement work as a paid services engagement.  We can be reached at [Sales@4Point.com](mailto:sales@4point.com).

## Contribution Process

### Submit an issue first

In general, it's a good idea to submit an issue indicating the change you are looking to make prior to beginning the coding process. This gives the maintainers a chance to comment on your proposed change.  They may be able to shed light on how to approach the change, what behaviour they would like to see and what behaviour they would not like to see.

### Make your change locally

The next step is to fork the project, create a branch in your local copy and then make the changes in the branch.  The general naming convention for branches is _<issue#>_-_<short description>_.  The short description allows someone to know what the branch is about without having to reference the initial issue.  The issue number allows someone to go back to the original issue if they need more information than the short description provides.

### Generate a Pull Request back to the original project

Once the change is complete (see coding standards below for what the requirements are to be "complete"), submit a PR back to the original project.  The PR will be reviewed.  If the review raises comments, then further changes may be asked for or required before the PR will be accepted.  Once those changes are made, the PR should be accepted and merged into the main code branch.  Thank you for your contribution!

## Coding Standards

These contributing standards have been put in place to ensure that contributions don’t increase the technical debt of the project nor disproportionately increase the maintenance effort on the project.  If a pull request does not meet these contributing guidelines, it will not be committed into the main branch of the project.  It will remain as a PR until someone (either the original contributor, a committer or some interested third party) devotes the required time to do the additional required work. If a PR is outstanding for too long, the original work may become out of sync with the main branch and the committers may decide it is no longer relevant.  In this case the PR will be closed without being committed to the main branch of the project.

### Code Format

The current code format mostly follows the default Eclipse Java formatting settings.  This includes 4 character tabs.  The only
change from defaults is the maximum line length which has been increased from 72 to 144.

### Code Structure

It is expected that the general class structure of new services created will mirror the existing class structure.  See the
[Development_CreatingARestServicesClient.md](Development_CreatingARestServicesClient.md) file for details.

### Unit Tests

It is expected that all new classes will have unit tests and that any changes to existing classes will require corresponding
changes to the associated unit test class.  The only exception to this is when a change to a class is internal only and has no externally visible changes to that classes API (i.e. it is only a refactoring).

The build should run all unit tests.  Unit tests should not require a running AEM instance, thus performing a build should not require a running AEM instance.

### Integration Tests

There is a specific integration test project (rest-services.it.tests).  Both the client and server side code bases have existing integration tests.  Any new code contributed is expected to also have associated integration tests in order to demonstrate that the code works when run an an actual AEM instance.

Whenever possible, please try and make the integration tests self-contained and independent of the AEM instance (with the only pre-condition being that the fluentforms and rest-services bundles are installed on the AEM instance).  In some cases this is not possible (for instance, Adaptive Forms require that the form reside on the AEM instance).  In most cases, however, you should be able to send all the required data to the server as part of a single, self-contained, transaction.

### Code Coverage

Before a PR is submitted, the contributor should run the unit tests with a code coverage tool:
* All non-exceptional code paths should be covered by the unit tests.  All results from those code paths should be validated.
* All exceptions generated by the validation of input parameters should be covered by unit tests.  The contents of the exception messages should be validated to make sure they contain all the relevant information to debug the issue.
* If possible, exception handling should also be covered by unit tests however this is not always possible.  Also, in some cases, the effort required to simulate a particular exception may be excessive.  In general, a PR will not be blocked by lack of exceptional code path coverage.
* Code statement coverage should be at least 80% but typically should be in the 90% range.

### Static Analysis

A static analysis tool (such as Spot Bugs or SonarQube) should be run on the code prior to submission as a PR.  It is not necessary to fix minor issues, but major issues should be addressed.
