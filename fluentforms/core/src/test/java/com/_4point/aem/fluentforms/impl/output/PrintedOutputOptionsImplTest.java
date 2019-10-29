package com._4point.aem.fluentforms.impl.output;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PrintedOutputOptionsImplTest {

	private final static com.adobe.fd.output.api.PrintedOutputOptions emptyPDFOutputOptions = new com.adobe.fd.output.api.PrintedOutputOptions();
	private final PrintedOutputOptionsImpl underTest = new PrintedOutputOptionsImpl();

	@Test
	@DisplayName("Make sure that if nothing was initialized, then the resulting options are the same as an empty options object.")
	void testToAdobePrintedOutputOptions_NoChanges() {
		
		assertEmpty(underTest, null);
	}

	private static void assertEmpty(PrintedOutputOptionsImpl printedOutputOptions, String contentRoot) {
		com.adobe.fd.output.api.PrintedOutputOptions adobePrintedOutputOptions = AdobeOutputServiceAdapter.toAdobePrintedOutputOptions(printedOutputOptions);
	}
}
