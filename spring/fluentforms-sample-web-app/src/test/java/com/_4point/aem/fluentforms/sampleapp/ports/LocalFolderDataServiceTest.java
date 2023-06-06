package com._4point.aem.fluentforms.sampleapp.ports;

import static com._4point.testing.matchers.javalang.ExceptionMatcher.exceptionMsgContainsAll;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com._4point.aem.fluentforms.sampleapp.domain.DataService.DataServiceException;

class LocalFolderDataServiceTest {
	
	private final LocalFolderDataService underTest;
	
	@TempDir 
	static Path testFolder;

	public LocalFolderDataServiceTest() {
		this.underTest = new LocalFolderDataService(testFolder);
	}

	@Test
	void testLoad_Preexisting() throws Exception {
		byte[] testData = "testLoad_Preexisting Test Data".getBytes();
		String key = "testLoad_Preexisting.txt";
		Files.write(testFolder.resolve(key), testData);
		byte[] result = underTest.load(key);
		assertArrayEquals(testData, result, "Test data retrieved should match what was written.");
	}

	@Test
	void testLoad_Nonexisting() {
		String key = "testLoad_Nonexisting.txt";
		DataServiceException ex = assertThrows(DataServiceException.class, ()->underTest.load(key));

		assertThat(ex, exceptionMsgContainsAll(testFolder.toString(), key, "Error while reading file"));
	}

	@Disabled
	@Test
	void testSave_Preexisting() {
		fail("Not yet implemented");
	}

	@Disabled
	@Test
	void testSave_Nonexisting() {
		fail("Not yet implemented");
	}

	@Disabled
	@Test
	void testSaveAndLoad() {
		fail("Not yet implemented");
	}
	
}
