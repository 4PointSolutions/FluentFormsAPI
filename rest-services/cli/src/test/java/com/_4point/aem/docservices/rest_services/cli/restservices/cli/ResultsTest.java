package com._4point.aem.docservices.rest_services.cli.restservices.cli;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.cli.restservices.cli.Results.Result;


class ResultsTest {

	@Test
	void testMediaTypeToString() {
		assertEquals("application/pdf", Results.MediaType.APPLICATION_PDF.toString());
		assertEquals("text/plain; charset=utf-8", Results.MediaType.TEXT_PLAIN.toString());
	}

	@Test
	void testResultSummary() {
		assertEquals("MediaType='application/pdf'", new Results.Result(null, Results.MediaType.APPLICATION_PDF, Optional.empty()).summary());
		assertEquals("MediaType='text/plain; charset=utf-8'; Filename='filename'", new Results.Result(null, Results.MediaType.TEXT_PLAIN, Optional.of(Path.of("filename"))).summary());
	}

	@Test
	void testDisplay_NoSecondaryResults() {
		String operation = "testDisplay_NoSecondaryResults";
		byte[] data = "Data Bytes".getBytes();
		Results underTest = Results.ofPdf(operation, data);
		
		List<String> result = underTest.display();
		assertNotNull(result);
		assertFalse(result.isEmpty());
		
		assertEquals(1, result.size());
		assertThat(result.get(0), allOf(containsString(operation), containsString(Results.MediaType.APPLICATION_PDF.toString())));
	}

	@Test
	void testDisplay_WithSecondaryResults() {
		String operation = "testDisplay_WithSecondaryResults";
		byte[] data = "Data Bytes".getBytes();
		String secondaryOperation = "testDisplay_SecondaryResult";
		String secondaryData = "Secondary Data Bytes";
		Results underTest = Results.builder(operation, Result.ofPdf(data))
								   .addSecondary(secondaryOperation, Result.ofText(secondaryData))
								   .build();
		
		List<String> result = underTest.display();
		assertNotNull(result);
		assertFalse(result.isEmpty());
		
		assertEquals(2, result.size());
		assertAll(
				()->assertThat(result.get(0), allOf(containsString(operation), containsString(Results.MediaType.APPLICATION_PDF.toString()))),
				()->assertThat(result.get(1), allOf(containsString(secondaryOperation), containsString(Results.MediaType.TEXT_PLAIN.toString())))
				);
	}

}
