package com._4point.aem.docservices.rest_services.cli.restservices.cli;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com._4point.aem.docservices.rest_services.cli.restservices.cli.Results.Result;

class ResultCommandsTest {

	private static final String operation = "testDisplay_WithSecondaryResults";
	private static final byte[] data = "Data Bytes".getBytes();
	private static final String secondaryOperation = "testDisplay_SecondaryResult";
	private static final String secondaryData = "Secondary Data Bytes";
	private static final Results mockResults = Results.build(operation, Result.ofPdf(data), b->b.addSecondary(secondaryOperation, Result.ofText(secondaryData)));

	private final ResultCommands underTest = new ResultCommands();
	static {
		ResultCommands.setResults(mockResults);
	}
	
	@Test
	void testResultsList() {
		String result = underTest.resultsList();
		assertThat(result, 
				allOf(
						containsString(operation), 
						containsString(Results.MediaType.APPLICATION_PDF.toString()),
						containsString(secondaryOperation), 
						containsString(Results.MediaType.TEXT_PLAIN.toString())
						)
				);
	}

	@Test
	void testResultsSave(@TempDir Path tempDir) throws Exception {
		Path saveFilePath = tempDir.resolve("ResultCommandsTest_testResultsSave_save.pdf");
		String result = underTest.resultsSave(1, saveFilePath);
		
		assertTrue(Files.exists(saveFilePath), "Save file did not get created.");
		assertArrayEquals(data, Files.readAllBytes(saveFilePath));

		assertThat(result, allOf(containsStringIgnoringCase("Saved to"), containsString(saveFilePath.toString())));
	}

	@Test
	void testResultsSaveSecondary(@TempDir Path tempDir) throws Exception {
		Path saveFilePath = tempDir.resolve("ResultCommandsTest_testResultsSaveSecondary_save.pdf");
		String result = underTest.resultsSave(2, saveFilePath);
		
		assertTrue(Files.exists(saveFilePath), "Save file did not get created.");
		assertArrayEquals(secondaryData.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(saveFilePath));

		assertThat(result, allOf(containsStringIgnoringCase("Saved to"), containsString(saveFilePath.toString())));
	}

	@Test
	void testResultsSave_NoParameter() throws Exception {
		String result = underTest.resultsSave(1, Path.of(""));
		
		assertThat(result, allOf(containsString("Save location must be specified"), containsString("Did not save results")));
	}

	@Test
	void testResultsSave_BadPathParameter() throws Exception {
		Path badPath = Path.of("foo/bar/doesnotexist.txt");
		String result = underTest.resultsSave(1, badPath);
		
		assertThat(result, allOf(containsString("Unexpected error occurred while saving"), containsString(badPath.toString()), containsString("Did not save results")));
	}

	@Test
	void testResultsDisplay() throws Exception {
		String result = underTest.resultsDisplay();
		
		assertNotNull(result);

		String lines[] = result.split("\\r?\\n");
		assertEquals(2, lines.length, "Expected two lines in result.");
		assertThat(lines[0], allOf(startsWith("1"), containsString(operation), containsString(Results.MediaType.APPLICATION_PDF.toString())));
		assertThat(lines[1], allOf(startsWith("2"), containsString(secondaryOperation), containsString(Results.MediaType.TEXT_PLAIN.toString())));
	}

	@Nested
	class NoResultsTests {
		// This class contains tests that occur before any reults have been recorded.
		
	}
}
