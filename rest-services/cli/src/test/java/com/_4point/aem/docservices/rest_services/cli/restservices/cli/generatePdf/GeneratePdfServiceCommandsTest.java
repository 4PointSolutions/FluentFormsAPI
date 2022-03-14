package com._4point.aem.docservices.rest_services.cli.restservices.cli.generatePdf;

import static com._4point.aem.docservices.rest_services.cli.restservices.cli.TestConstants.SAMPLE_FILES_DIR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com._4point.aem.docservices.rest_services.cli.restservices.cli.ResultUtils;
import com._4point.aem.docservices.rest_services.cli.restservices.cli.Results;
import com._4point.aem.docservices.rest_services.cli.restservices.cli.Results.MediaType;
import com._4point.aem.docservices.rest_services.cli.restservices.cli.Results.Result;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.generatePDF.ExceptionalMockTraditionalGeneratePDFService;
import com._4point.aem.fluentforms.testing.generatePDF.MockTraditionalGeneratePDFService;

/**
 * This class tests GeneratePdfServiceCommands
 *
 * Note:  Because GeneratePdfServiceCommands uses Results (which contains a singleton) and because these tests
 *        test the effects on that singleton, these tests cannot be run in parallel threads.
 *        
 *        Also, note that some of the tests throw exceptions which are logged, so the tests produce stack traces
 *        in the output.  This is to be expected.
 */
class GeneratePdfServiceCommandsTest {

	@BeforeEach
	void setup() {
		ResultUtils.clearResults();
	}

	@Nested
	class HappyPath {
		private byte[] mockDocumentContents = "Mock Document Contents".getBytes(StandardCharsets.UTF_8);
		private Document resultDocument = MockDocumentFactory.GLOBAL_INSTANCE.create(mockDocumentContents);
		private byte[] mockLogContents = "Mock Log Contents".getBytes(StandardCharsets.UTF_8);
		private Document resultLog = MockDocumentFactory.GLOBAL_INSTANCE.create(mockLogContents);
		private CreatePDFResult mockResult = new CreatePDFResult() {
			
			@Override
			public void close() throws IOException {
			}
			
			@Override
			public Document getLogDocument() {
				return resultLog;
			}
			
			@Override
			public Document getCreatedDocument() {
				return resultDocument;
			}
		};
	
		private final GeneratePdfServiceCommands underTest = new GeneratePdfServiceCommands(
				()->MockTraditionalGeneratePDFService.createGeneratePDFMock(mockResult)
				);

		@Test
		void testCreatePdf_NoOutput() throws Exception {
			Path testDoc = SAMPLE_FILES_DIR.resolve("Sample1.docx");
			
			String result = underTest.createPdf(testDoc, Path.of(""));
			
			assertThat(result, containsStringIgnoringCase("Document converted to pdf"));
			
			// Validate the stored Results
			Results results = ResultUtils.getResults().orElseThrow();
			assertEquals("create-pdf-output", results.operation());
			ResultUtils.validateResult(results.primary(), mockDocumentContents, MediaType.APPLICATION_PDF);
			Map<String, Result> secondaries = results.secondary();
			assertEquals(1, secondaries.size());
			ResultUtils.validateResult(secondaries.get("create-pdf-log"), mockLogContents, MediaType.TEXT_PLAIN);
		}

		@Test
		void testCreatePdf_OutputToFile(@TempDir Path tempDir) throws Exception {
			Path testDoc = SAMPLE_FILES_DIR.resolve("Sample1.docx");
			Path output = tempDir.resolve("GeneratePdfServiceCommandsTest_testCreatePdf_OutputToFile.pdf");
			
			String result = underTest.createPdf(testDoc, output);
			
			assertThat(result, containsStringIgnoringCase("Document converted to pdf"));
			
			// Validate the stored Results
			Results results = ResultUtils.getResults().orElseThrow();
			assertEquals("create-pdf-output", results.operation());
			ResultUtils.validateResult(results.primary(), mockDocumentContents, MediaType.APPLICATION_PDF);
			Map<String, Result> secondaries = results.secondary();
			assertEquals(1, secondaries.size());
			ResultUtils.validateResult(secondaries.get("create-pdf-log"), mockLogContents, MediaType.TEXT_PLAIN);
			
			// Validate file was written to disk
			ResultUtils.validateOutput(output, mockDocumentContents);
		}

	}	

	@Nested
	class GeneratePdfServiceCommandsTest_Exceptions {
		private static final String MOCK_EXCEPTION_MESSAGE = "Mock Exception Message.";
		private final GeneratePdfServiceCommands underTest = new GeneratePdfServiceCommands(
				()->ExceptionalMockTraditionalGeneratePDFService.create(MOCK_EXCEPTION_MESSAGE)
				);

		@BeforeEach
		void setup() {
			ResultUtils.clearResults();
		}

		@Test
		void testCreatePdf() {
			Path testDoc = SAMPLE_FILES_DIR.resolve("Sample1.docx");
			
			String result = underTest.createPdf(testDoc, Path.of(""));
			assertThat(result, allOf(containsString(MOCK_EXCEPTION_MESSAGE), containsStringIgnoringCase("Conversion to PDF failed")));
			assertTrue(ResultUtils.getResults().isEmpty());
		}
		
	}

}
