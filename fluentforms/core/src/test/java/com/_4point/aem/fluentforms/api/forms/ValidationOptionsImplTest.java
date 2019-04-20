package com._4point.aem.fluentforms.api.forms;

import static com._4point.aem.fluentforms.api.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.impl.forms.ValidationOptionsImpl;

class ValidationOptionsImplTest {

	private ValidationOptions underTest;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testToAdobeValidationOptions() throws FileNotFoundException {
		underTest = new ValidationOptionsImpl(SAMPLE_FORMS_DIR, SAMPLE_FORMS_DIR);
		com.adobe.fd.forms.api.ValidationOptions adobeValidationOptions = underTest.toAdobeValidationOptions();
		assertEquals(SAMPLE_FORMS_DIR.toString(), adobeValidationOptions.getContentRoot());
		assertEquals(SAMPLE_FORMS_DIR.toString(), adobeValidationOptions.getDebugDir());
	}

	@Test
	@DisplayName("Make sure bad filenames throw FileNotFoundExceptions")
	void testValidationOptions_nullArguments() {
		Path badFilename = Paths.get("foo", "bar");
		FileNotFoundException ex1 = assertThrows(FileNotFoundException.class, ()->new ValidationOptionsImpl(badFilename, SAMPLE_FORMS_DIR));
		String msg1 = ex1.getMessage();
		assertTrue(msg1.contains("content root"), "Expected exception message to contain 'content root' (" + msg1 + ").");
		assertTrue(msg1.contains(badFilename.toString()), "Expected exception message to contain bad filename ' + badFilename.toString() + ' (" + msg1 + ").");
		
		FileNotFoundException ex2 = assertThrows(FileNotFoundException.class, ()->new ValidationOptionsImpl(SAMPLE_FORMS_DIR, badFilename));
		String msg2 = ex2.getMessage();
		assertTrue(msg2.contains("debug dumps"), "Expected exception message to contain 'debug dumps' (" + msg2 + ").");
		assertTrue(msg2.contains(badFilename.toString()), "Expected exception message to contain bad filename ' + badFilename.toString() + ' (" + msg2 + ").");

	}

	@Test
	void testToAdobeValidationOptions_nullArguments() throws FileNotFoundException {
		underTest = new ValidationOptionsImpl(null, null);
		com.adobe.fd.forms.api.ValidationOptions adobeValidationOptions = underTest.toAdobeValidationOptions();
		assertNull(adobeValidationOptions.getContentRoot());
		assertNull(adobeValidationOptions.getDebugDir());
	}

}
