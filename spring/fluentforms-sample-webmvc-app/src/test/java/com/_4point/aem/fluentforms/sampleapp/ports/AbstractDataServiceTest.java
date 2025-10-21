package com._4point.aem.fluentforms.sampleapp.ports;

import static com._4point.testing.matchers.javalang.ExceptionMatchers.exceptionMsgContainsAll;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.sampleapp.domain.DataService;
import com._4point.aem.fluentforms.sampleapp.domain.DataService.DataServiceException;

public abstract class AbstractDataServiceTest {

	private final DataService underTest;
	private final String[] loadExceptionExpectedStrings;	// Additional Strings we expect to see in exceptions thrown by load()
	private final String[] saveExceptionExpectedStrings;	// Additional Strings we expect to see in exceptions thrown by save()

	protected AbstractDataServiceTest(DataService underTest, String[] loadExceptionExpectedStrings, String[] saveExceptionExpectedStrings ) {
		this.underTest = underTest;
		this.loadExceptionExpectedStrings = loadExceptionExpectedStrings;
		this.saveExceptionExpectedStrings = saveExceptionExpectedStrings;
	}

	@DisplayName("Loading from pdf-existing file should work.")
	@Test
	void testLoad_Preexisting() throws Exception {
		byte[] testData = "testLoad_Preexisting Test Data".getBytes();
		String key = "testLoad_Preexisting.txt";
		underTest.save(key, testData);;
		
		byte[] result = underTest.load(key);
		
		assertArrayEquals(testData, result, "Test data retrieved should match what was written.");
	}

	@DisplayName("Loading from non-existing file should create exception containing filenames.")
	@Test
	void testLoad_Nonexisting() {
		String key = "testLoad_Nonexisting.txt";
		
		DataServiceException ex = assertThrows(DataServiceException.class, ()->underTest.load(key));

		assertThat(ex, exceptionMsgContainsAll(key, loadExceptionExpectedStrings));
	}

	@Test
	void testSave_Preexisting() throws Exception {
		byte[] testData = "testSave_Preexisting Test Data".getBytes();
		String key = "testSave_Preexisting.txt";
		underTest.save(key, testData);
		
		DataServiceException ex = assertThrows(DataServiceException.class, ()->underTest.save(key, testData));
		
		assertThat(ex, exceptionMsgContainsAll(key, saveExceptionExpectedStrings));
	}

	@DisplayName("Saving to non-existing file should work.")
	@Test
	void testSave_Nonexisting() throws Exception {
		byte[] testData = "testSave_Nonexisting Test Data".getBytes();
		String key = "testSave_Nonexisting.txt";
		
		underTest.save(key, testData);
		
		byte[] result = underTest.load(key);
		assertArrayEquals(testData, result, "Test data retrieved should match what was written.");
	}
	
	@Test
	void testExists_Preexisting() throws Exception {
		byte[] testData = "testExists_Preexisting Test Data".getBytes();
		String key = "testExists_Preexisting.txt";
		underTest.save(key, testData);

		assertTrue(underTest.exists(key), "Key should exist but it does not.");
	}

	@DisplayName("Saving to non-existing file should work.")
	@Test
	void testExists_Nonexisting() throws Exception {
		String key = "testExists_Nonexisting.txt";
		
		assertFalse(underTest.exists(key), "Key should not exist but it does.");
	}

}