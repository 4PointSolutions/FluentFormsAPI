package com._4point.aem.docservices.rest_services.cli.restservices.cli.output;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.testing.output.MockOutputService;
import com._4point.aem.fluentforms.testing.output.MockTraditionalOutputService;

class OutputServiceCommandsTest {

	
	@Mock private Document resultDocument;
	private final OutputServiceCommands underTest = new OutputServiceCommands(()->MockTraditionalOutputService.createDocumentMock(resultDocument));
	
	@Test
	void testGeneratePDFOutput() {
		fail("Not yet implemented");
	}

	@Test
	void testGeneratePDFOutputRemote() {
		fail("Not yet implemented");
	}

}
