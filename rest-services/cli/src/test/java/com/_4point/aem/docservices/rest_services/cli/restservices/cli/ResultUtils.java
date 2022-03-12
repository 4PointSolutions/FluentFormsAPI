package com._4point.aem.docservices.rest_services.cli.restservices.cli;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com._4point.aem.docservices.rest_services.cli.restservices.cli.Results.MediaType;
import com._4point.aem.docservices.rest_services.cli.restservices.cli.Results.Result;

public class ResultUtils {
	public static Optional<Results> getResults() {
		return Optional.ofNullable(ResultCommands.getResults());
	}
	
	public static void clearResults() {
		ResultCommands.setResults(null);
	}
	
	public static void validateResult(Result result, byte[] expectedData, MediaType expectedMediaType) {
		assertNotNull(result);
		assertAll(
				()->assertArrayEquals(expectedData, result.data()),
				()->assertEquals(expectedMediaType, result.mediaType()),
				()->assertTrue(result.filename().isEmpty())
				);
	}
	
	public static void validateResult(Result result, byte[] expectedData, MediaType expectedMediaType, Path expectedFilename) {
		assertNotNull(result);
		assertAll(
				()->assertArrayEquals(expectedData, result.data()),
				()->assertEquals(expectedMediaType, result.mediaType()),
				()->assertEquals(expectedFilename, result.filename().orElseThrow())
				);
	}

	public static void validateOutput(Path output, byte[] expectedContents) throws IOException {
		assertTrue(Files.exists(output), "Output file was not created.");
		assertArrayEquals(expectedContents, Files.readAllBytes(output));
	}
}
