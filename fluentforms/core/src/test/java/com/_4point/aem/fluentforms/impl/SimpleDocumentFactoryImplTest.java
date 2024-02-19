package com._4point.aem.fluentforms.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.Document;

class SimpleDocumentFactoryImplTest {

	// Test that the document interface is fluent.
	@Test
	void testDocumentFluency() throws Exception {
		try (Document underTest = SimpleDocumentFactoryImpl.getFactory().create("TestData".getBytes())) {
			String someValue = "SomeValue";
			String anotherValue = "AnotherValue";
			String attr1 = "attr1";
			String attr2 = "attr2";
			underTest.setContentType("application/pdf")
			 		 .setAttribute(attr1, someValue)
					 .setContentType("application/xml")
					 .setAttributeAsString(attr2, anotherValue)
					 .setContentType("application/xml")
					 .removeAttribute(attr1)
					 ;
			
			assertEquals(underTest.getAttribute(attr2), anotherValue);
			assertNull(underTest.getAttribute(attr1));
			assertFalse(underTest.getOptionalAttributeAsString(attr1).isPresent());
		}
	}

	// Test that the document interface is fluent.
	@Test
	void testDocumentPageCount() throws Exception {
		Long expectedPageCount = 23L;
		try (Document underTest = SimpleDocumentFactoryImpl.getFactory().create("TestData".getBytes())) {
			underTest.setPageCount(expectedPageCount);
			assertEquals(expectedPageCount, underTest.getPageCount().get());
		}
	}
}
