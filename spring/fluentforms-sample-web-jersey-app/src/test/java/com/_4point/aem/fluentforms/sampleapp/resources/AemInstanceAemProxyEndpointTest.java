package com._4point.aem.fluentforms.sampleapp.resources;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;

import com._4point.aem.fluentforms.sampleapp.FluentFormsSpringApplication;

@EnabledIf("com._4point.aem.fluentforms.sampleapp.resources.TestConstants#runAemInstanceTests")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
				classes = FluentFormsSpringApplication.class
				)
@ContextConfiguration(initializers = AemInstance.AemInstanceContextInitializer.class)
public class AemInstanceAemProxyEndpointTest extends AbstractAemProxyEndpointTest {
	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms/";

	protected AemInstanceAemProxyEndpointTest() {
		super(CRX_CONTENT_ROOT + SAMPLE_XDP_FILENAME_PATH);
	}

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	@Override
	protected void verifyProxyTest() {
		// Can't really verify that the calls were made to AEM (other than no errors occurred).
	}

}
