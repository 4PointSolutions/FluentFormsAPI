package com._4point.aem.docservices.rest_services.cli.restservices.cli.output;

import static com._4point.aem.docservices.rest_services.cli.restservices.cli.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.testing.output.ExceptionalMockTraditionalOutputService;
import com._4point.aem.fluentforms.testing.output.MockTraditionalOutputService;

class OutputServiceCommandsTest {

	@Nested
	class OutputServiceCommandsTest_HappyPath {
		@Mock
		private Document resultDocument;
		private final OutputServiceCommands underTest = new OutputServiceCommands(
				() -> MockTraditionalOutputService.createDocumentMock(resultDocument)
				);

		@Test
		void testGeneratePDFOutput() {
			Path template = SAMPLE_FILES_DIR.resolve("SampleForm.xdp");
			Path data = SAMPLE_FILES_DIR.resolve("SampleForm_data.xml");
			String result = underTest.generatePDFOutput(template, data);
			assertThat(result, containsStringIgnoringCase("Document generated"));
		}

		@Test
		void testGeneratePDFOutputRemote() {
			Path template = SAMPLE_FILES_DIR.resolve("SampleForm.xdp");
			Path data = SAMPLE_FILES_DIR.resolve("SampleForm_data.xml");
			String result = underTest.generatePDFOutputRemote(PathOrUrl.from(template), data);
			assertThat(result, containsStringIgnoringCase("Document generated"));
		}
	}

	@Nested
	class OutputServiceCommandsTest_Exceptions {
		private static final String MOCK_EXCEPTION_MESSAGE = "Mock Exception Message.";
		private final OutputServiceCommands underTest = new OutputServiceCommands(
				() -> ExceptionalMockTraditionalOutputService.create(MOCK_EXCEPTION_MESSAGE)
				);

		@Test
		void testGeneratePDFOutput() {
			Path template = SAMPLE_FILES_DIR.resolve("SampleForm.xdp");
			Path data = SAMPLE_FILES_DIR.resolve("SampleForm_data.xml");
			String result = underTest.generatePDFOutput(template, data);
			assertThat(result, allOf(containsString(MOCK_EXCEPTION_MESSAGE), containsStringIgnoringCase("Document generation failed")));
		}

		@Test
		void testGeneratePDFOutputRemote() {
			Path template = SAMPLE_FILES_DIR.resolve("SampleForm.xdp");
			Path data = SAMPLE_FILES_DIR.resolve("SampleForm_data.xml");
			String result = underTest.generatePDFOutputRemote(PathOrUrl.from(template), data);
			assertThat(result, allOf(containsString(MOCK_EXCEPTION_MESSAGE), containsStringIgnoringCase("Document generation failed")));
		}
	}
}
