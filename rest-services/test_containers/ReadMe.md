In order to create an integration test container image, using the `ff_it_test` dockerfile, perform the following steps:

1. Create the `deploy_it_assets.jar` file in `ff_it_files` by following the "Creating deploy_it_assets.jar" instructions in `jbang_scripts\README.md`. 
(This step is required because the .jar is not stored in the GitHub repository but it is required in order for the dockerfile to run.)
2. Alter the first line of the `ff_it_test.dockerfile` file to use the desired AEM base container image.
3. Run `docker buildx build --file ff_it_test.dockerfile -t aem_lts_it_tests:aem65lts_it_tests .` to create the integration test image.

After performing these steps then the integration tests should run successfully after `TestUtils.java` is modified to set 
`AEM_TARGET_TYPE` to be `AemTargetType.TESTCONTAINERS`.  If the name of the integration test container image is
different than the one in the steps above, then `AEM_IMAGE_NAME` may also need to be modified.