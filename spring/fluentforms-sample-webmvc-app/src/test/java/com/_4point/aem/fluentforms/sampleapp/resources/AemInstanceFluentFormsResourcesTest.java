package com._4point.aem.fluentforms.sampleapp.resources;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;

import com._4point.aem.fluentforms.sampleapp.FluentFormsSpringApplication;

/**
 * These AEM Instance tests run against a running AEM instance that have sample forms deployed to the same locations
 * as the rest-serivices it.tests tests.  In practice, this means that (for example) the test containers docker image
 * should be the it.tests docker image. 
 */
@EnabledIf("com._4point.aem.fluentforms.sampleapp.resources.TestConstants#runAemInstanceTests")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
				classes = FluentFormsSpringApplication.class
				)
@ContextConfiguration(initializers = AemInstance.AemInstanceContextInitializer.class)
public class AemInstanceFluentFormsResourcesTest extends AbstractFluentFormsResourcesTest {

	protected AemInstanceFluentFormsResourcesTest() {
		super(AemInstance.AEM_1.samplesPath(SAMPLE_XDP_FILENAME_PATH).toString());
	}

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}
}
