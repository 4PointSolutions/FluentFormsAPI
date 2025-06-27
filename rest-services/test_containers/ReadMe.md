In order to create an integration test container image, using the `ff_it_test` dockerfile, perform the following steps:

1. Alter the first line of the `ff_it_test.dockerfile` file to use the desired AEM base container image.
2. Run `docker buildx build --file ff_it_test.dockerfile -t aem_lts_it_tests:aem65lts_it_tests .` to create the integration test image.