package com._4point.aem.fluentforms.sampleapp.ports;

import static com._4point.testing.matchers.javalang.ExceptionMatchers.exceptionMsgContainsAll;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
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

	@DisplayName("Loading from pdf-existing file should work.")
	@Test
	void testLoad_Preexisting() throws Exception {
		byte[] testData = "testLoad_Preexisting Test Data".getBytes();
		String key = "testLoad_Preexisting.txt";
		createFile(testData, key);
		
		byte[] result = underTest.load(key);
		
		assertArrayEquals(testData, result, "Test data retrieved should match what was written.");
	}

	void createFile(byte[] testData, String key) throws IOException {
		Files.write(testFolder.resolve(key), testData);
	}

	@DisplayName("Loading from non-existing file should create exception containing filenames.")
	@Test
	void testLoad_Nonexisting() {
		String key = "testLoad_Nonexisting.txt";
		
		DataServiceException ex = assertThrows(DataServiceException.class, ()->underTest.load(key));

		assertThat(ex, exceptionMsgContainsAll(testFolder.toString(), key, "Error while reading file"));
	}

	@Test
	void testSave_Preexisting() throws Exception {
		byte[] testData = "testLoad_Preexisting Test Data".getBytes();
		String key = "testLoad_Preexisting.txt";
		createFile(testData, key);
		
		DataServiceException ex = assertThrows(DataServiceException.class, ()->underTest.save(key, testData));
		
		assertThat(ex, exceptionMsgContainsAll(testFolder.toString(), key, "Error while writing file"));
	}

	@DisplayName("Saving to non-existing file should work.")
	@Test
	void testSave_Nonexisting() throws Exception {
		byte[] testData = "testSave_Nonexisting Test Data".getBytes();
		String key = "testSave_Nonexisting.txt";
		
		underTest.save(key, testData);
		
		byte[] result = Files.readAllBytes(testFolder.resolve(key));
		assertArrayEquals(testData, result, "Test data retrieved should match what was written.");
	}

	@DisplayName("Saving and loading should be a taransparent operation (data in should equal data out).")
	@Test
	void testSaveAndLoad() {
		byte[] testData = "testSaveAndLoad Test Data".getBytes();
		String key = "testSaveAndLoad.txt";
		
		underTest.save(key, testData);
		byte[] result = underTest.load(key);
		
		assertArrayEquals(testData, result);
	}
	
}
