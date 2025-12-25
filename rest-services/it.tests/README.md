# FluentForms REST Services Integration Test Project

This maven project contains the Integration tests that run against (and require) a real AEM instance.

The types of AEM instances that are supported are: Local AEM instance, Remote AEM instance (Windows or Linux), Containerized AEM instance (via [TestContainers](https://testcontainers.com/)).  Depending on the type of AEM instance, some changes to the source code may be required.

The tests make sure Aem is up and running before they start, so you can launch the AEM instance, followed immediately by the tests.  The tests will wait for AEM to come up before they run (or timeout if AEM is not running within 5 minutes of the tests being launched.)

## Preparing to run the tests

All tests assume that AEM has been installed and configured with the AEM Forms Add-on, FluentForms .jars have been installed, and that HTML5 Protected Mode has been turned off.  It also assumes that the `sling.properties` has been updated with the JSAFE setting per the [Adobe documentation](https://experienceleague.adobe.com/en/docs/experience-manager-65-lts/content/forms/install-aem-forms/osgi-installation/install-configure-document-services#configure-boot-delegation-for-rsa-bouncycastle-libraries).

There are some tests that assume that PDFG has been installed and configured.  These are tagged with the `requiresPdfG` tag, so you 
can configure JUnit to exclude these tests if PDFG has not been installed.

There are some tests that assume that a Reader Extensions credential called `recred` has been installed and configured.  
These are tagged with the `requiresRe` tag, so you can configure JUnit to exclude these tests if the Reader Extensions credential has not been installed.

The tests also assume that the sample files are available on the machine running the AEM instance.  This is automatically true for Local AEM instances, but the sample files (under `test_containers/ff_it_files`) must be transferred manually for remote AEM instances.  On Windows machines the samples should be placed under `\Adobe\ff_it_files`.  On Linux machines, they should be under `/opt/adobe/ff_it_files`.  This follows the 4Point standard practice of installing AEM under `\Adobe\AEM_##_SP##` on Windows machines and under `/opt/adobe/AEM_##_SP##` on Linux machines.

### Local AEM Instance
This is the default scenario.   The tests assume that a properly configured instance of AEM is up and running on port 4502.
They assume that the default username/password (`admin`/`admin`) are available.

You can change any of the defaults by changing the constants in `TestUtils.java`. 

### Remote AEM Instance
You can configure the tests to run against a remote AEM instance by altering `AEM_TARGET_TYPE` in `TestUtils.java` to `AemTargetType.REMOTE_WINDOWS` or `AemTargetType.REMOTE_LINUX`.  NOTE: the tests assume that AEM has been configured as outlined above (with FluentForms, protected mode, sample files, etc.).

The machine name and port of the remote instance must be set in `TestUtils.java` via the `TEST_MACHINE_NAME` and `TEST_MACHINE_PORT` constants.  If the default username/password (`admin`/`admin`) are not available, then the `TEST_USER` amd `TEST_USER_PASSWORD` constants in that file must also be updated with a valid username/password.

### Containerized AEM Image (via TestContainers)
You can configure the tests to run against AEM running in a local container image by altering `AEM_TARGET_TYPE` in `TestUtils.java` to `AemTargetType.TESTCONTAINERS`. 
 
 The name of the container image must be configured via the `AEM_IMAGE_NAME` constant in `TestUtils.java`.  Since AEM is 
 proprietary software, a pre-prepared image cannot be made available publicly, however this project contains a dockerfile (`test_containers/ff_it_test.dockerfile`) that can take a base AEM instance and copy the sample files to it.
 See the README in that directory for more details. 
    
The TestContainers code assumes that the image contains an AEM instance running on port 4502.  The TestContainers code is located in the `AemInstance.java` source file. 

#### Creating a Containerized Image using `aem_cntrl`

In order to create a containerized AEM integration test image...
* Create the FluentForms jar files (`fluentforms.core-0.0.x-SNAPSHOT.jar`, and `rest-services.server-0.0.x-SNAPSHOT.jar`) from their projects within this GitHub repo.
* Download the latest AEM installation files (quickstart jar plus any service pack zips).
* Obtain a copy of your `licence.properties` file from you current AEM installation.
* Create an `aem_cntrl-0.0.x-SNAPSHOT.jar` file from the [`aem_cntrl` project](https://github.com/4PointSolutions/aem-utils/tree/main/aem_cntrl).
* Follow the instructions in the [`aem_container` project](https://github.com/4PointSolutions/aem-utils/blob/main/aem_container/README.md) to create a base AEM container image.
* Follow the instructions in the [test_containers ReadMe](https://github.com/4PointSolutions/FluentFormsAPI/blob/master/rest-services/test_containers/ReadMe.md) to create an integration test container image from the base AEM container image.

Once the container is built, configure the project as [outlined above](#containerized-aem-image-via-testcontainers) and run the tests.

### Choosing Local Instance vs Containerized Instance vs Remote Instance

Using a local AEM instance is convenient when developing the server-side libraries (`fluentforms.core` and `rest-services.server`) 
because deployment of new libraries is easy (just copy the new `.jar` files into the local AEM instance's install directory.

Using a containerized AEM instance is convenient for testing new versions of AEM as the installation and configuration of AEM 
is automated by the docker instance creation.

Using Remote AEM instances is useful when you have an existing AEM instance running on a remote server that you wish to test
in order to make sure AEM is installed, configured, and functioning correctly.

## Testing New Releases of AEM

Each time a new version of AEM is released, the integration tests in this project should be run against it.

To do this, follow the instructions under [Creating a Containerized Image using `aem_cntrl`](#creating-a-containerized-image-using-aem_cntrl) and then
running the tests as outlined under [Containerized AEM Image \(via TestContainers\)](#containerized-aem-image-via-testcontainers)