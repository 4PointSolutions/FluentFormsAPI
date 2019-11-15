package com._4point.aem.docservices.rest_services.client.output;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;

class RestServicesOutputServiceAdapterTest {

	private final static Document DUMMY_TEMPLATE_DOC = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;
	private final static String DUMMY_TEMPLATE_STR = "";
	private final static Document DUMMY_DATA = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;
	
	RestServicesOutputServiceAdapter underTest = RestServicesOutputServiceAdapter.builder().build();
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGeneratePDFOutput_NullArguments() throws Exception {
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput((Document)null, null, null));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("template"));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("cannot be null"));
		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput((String)null, null, null));
		assertThat(ex2.getMessage(), containsStringIgnoringCase("template"));
		assertThat(ex2.getMessage(), containsStringIgnoringCase("cannot be null"));
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(DUMMY_TEMPLATE_DOC, null, null));
		assertThat(ex3.getMessage(), containsStringIgnoringCase("PdfOutputOptions"));
		assertThat(ex3.getMessage(), containsStringIgnoringCase("cannot be null"));
		NullPointerException ex4 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(DUMMY_TEMPLATE_STR, null, null));
		assertThat(ex4.getMessage(), containsStringIgnoringCase("PdfOutputOptions"));
		assertThat(ex4.getMessage(), containsStringIgnoringCase("cannot be null"));
	}

}
