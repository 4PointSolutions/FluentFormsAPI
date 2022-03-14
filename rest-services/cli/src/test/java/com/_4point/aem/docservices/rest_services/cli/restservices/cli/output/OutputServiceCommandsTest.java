package com._4point.aem.docservices.rest_services.cli.restservices.cli.output;

import static com._4point.aem.docservices.rest_services.cli.restservices.cli.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com._4point.aem.docservices.rest_services.cli.restservices.cli.ResultCommands;
import com._4point.aem.docservices.rest_services.cli.restservices.cli.ResultUtils;
import com._4point.aem.docservices.rest_services.cli.restservices.cli.Results.MediaType;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.output.ExceptionalMockTraditionalOutputService;
import com._4point.aem.fluentforms.testing.output.MockTraditionalOutputService;

/**
 * This class tests OutputServiceCommands
 *
 * Note:  Because OutputServiceCommands uses Results (which contains a singleton) and because these tests
 *        test the effects on that singleton, these tests cannot be run in parallel threads.
 *        
 *        Also, note that some of the tests throw exceptions which are logged, so the tests produce stack traces
 *        in the output.  This is to be expected.
 */
class OutputServiceCommandsTest {

	@Nested
	class OutputServiceCommandsTest_HappyPath {
		private byte[] mockDocumentContents = "Mock Document Contents".getBytes(StandardCharsets.UTF_8);
		private Document resultDocument = MockDocumentFactory.GLOBAL_INSTANCE.create(mockDocumentContents);
		
		private final OutputServiceCommands underTest = new OutputServiceCommands(
				() -> MockTraditionalOutputService.createDocumentMock(resultDocument)
				);

		@BeforeEach
		void setup() {
			ResultUtils.clearResults();
		}

		@Test
		void testGeneratePDFOutput_NoOutput() throws Exception {
			Path template = SAMPLE_FILES_DIR.resolve("SampleForm.xdp");
			Path data = SAMPLE_FILES_DIR.resolve("SampleForm_data.xml");
			
			String result = underTest.generatePDFOutput(template, data, Path.of(""));
			
			assertThat(result, containsStringIgnoringCase("Document generated"));
			ResultUtils.validateResult(ResultUtils.getResults().orElseThrow().primary(), mockDocumentContents, MediaType.APPLICATION_PDF);
		}

		@Test
		void testGeneratePDFOutput_OutputToFile(@TempDir Path tempDir) throws Exception {
			Path template = SAMPLE_FILES_DIR.resolve("SampleForm.xdp");
			Path data = SAMPLE_FILES_DIR.resolve("SampleForm_data.xml");
			Path output = tempDir.resolve("OutputServiceCommandsTest_testGeneratePDFOutput_OutputToFile.pdf");
			
			String result = underTest.generatePDFOutput(template, data, output);
			
			assertThat(result, containsStringIgnoringCase("Document generated"));
			ResultUtils.validateOutput(output, mockDocumentContents);
			ResultUtils.validateResult(ResultUtils.getResults().orElseThrow().primary(), mockDocumentContents, MediaType.APPLICATION_PDF);
		}

		@Test
		void testGeneratePDFOutputRemote() throws Exception {
			Path template = SAMPLE_FILES_DIR.resolve("SampleForm.xdp");
			Path data = SAMPLE_FILES_DIR.resolve("SampleForm_data.xml");

			String result = underTest.generatePDFOutputRemote(PathOrUrl.from(template), data, Path.of(""));

			assertThat(result, containsStringIgnoringCase("Document generated"));
			ResultUtils.validateResult(ResultUtils.getResults().orElseThrow().primary(), mockDocumentContents, MediaType.APPLICATION_PDF);
		}
	}

	@Nested
	class OutputServiceCommandsTest_Exceptions {
		private static final String MOCK_EXCEPTION_MESSAGE = "Mock Exception Message.";
		private final OutputServiceCommands underTest = new OutputServiceCommands(
				() -> ExceptionalMockTraditionalOutputService.create(MOCK_EXCEPTION_MESSAGE)
				);

		@BeforeEach
		void setup() {
			ResultUtils.clearResults();
		}

		@Test
		void testGeneratePDFOutput() {
			Path template = SAMPLE_FILES_DIR.resolve("SampleForm.xdp");
			Path data = SAMPLE_FILES_DIR.resolve("SampleForm_data.xml");
			String result = underTest.generatePDFOutput(template, data, Path.of(""));
			assertThat(result, allOf(containsString(MOCK_EXCEPTION_MESSAGE), containsStringIgnoringCase("Document generation failed")));
			assertTrue(ResultUtils.getResults().isEmpty(), "Expected results to be empty but they were not.");
		}

		@Test
		void testGeneratePDFOutputRemote() {
			Path template = SAMPLE_FILES_DIR.resolve("SampleForm.xdp");
			Path data = SAMPLE_FILES_DIR.resolve("SampleForm_data.xml");
			String result = underTest.generatePDFOutputRemote(PathOrUrl.from(template), data, Path.of(""));
			assertThat(result, allOf(containsString(MOCK_EXCEPTION_MESSAGE), containsStringIgnoringCase("Document generation failed")));
			assertTrue(ResultUtils.getResults().isEmpty(), "Expected results to be empty but they were not.");
		}
	}
}
