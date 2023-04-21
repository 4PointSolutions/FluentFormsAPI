package com._4point.aem.fluentforms.impl.assembler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec.ResultLevel;

class PDFAValidationOptionSpecImplTest {
	private static final com.adobe.fd.assembler.client.PDFAValidationOptionSpec emptyAdobePDFAValidationOptionSpec = new com.adobe.fd.assembler.client.PDFAValidationOptionSpec();;
	
	private final PDFAValidationOptionSpecImpl underTest = new PDFAValidationOptionSpecImpl();

	@Test
	@DisplayName("Make sure that if nothing was initialized, then the resulting options are the same as an empty options object.")
	void testEmptyAdobePDFAValidationOptionSpecImpl() {
		assertEmpty(underTest);
	}

	private void assertEmpty(PDFAValidationOptionSpecImpl pdfaValidationOptionSpecImpl) {
		com.adobe.fd.assembler.client.PDFAValidationOptionSpec adobePDFAValidationOptionSpec = AdobeAssemblerServiceAdapter.toAdobePDFAValidationOptionSpec(pdfaValidationOptionSpecImpl);

		assertAll(
				()->assertEquals(emptyAdobePDFAValidationOptionSpec.getCompliance(), adobePDFAValidationOptionSpec.getCompliance()),
				()->assertEquals(emptyAdobePDFAValidationOptionSpec.getLogLevel(), adobePDFAValidationOptionSpec.getLogLevel()),
				()->assertEquals(emptyAdobePDFAValidationOptionSpec.getResultLevel(), adobePDFAValidationOptionSpec.getResultLevel()),
				()->assertEquals(emptyAdobePDFAValidationOptionSpec.isAllowCertificationSignatures(), adobePDFAValidationOptionSpec.isAllowCertificationSignatures()),
				()->assertEquals(emptyAdobePDFAValidationOptionSpec.isIgnoreUnusedResource(), adobePDFAValidationOptionSpec.isIgnoreUnusedResource())
				);
	}
	
	@Test
	@DisplayName("Make sure that if everything was initialized, then the resulting options are the not same as an empty options object.")
	void testToAdobePDFAValidationOptionSpec_AllChanges() {
		underTest.setAllowCertificationSignatures(false);
		underTest.setCompliance(Compliance.PDFA_2B);
		underTest.setIgnoreUnusedResource(false);
		underTest.setLogLevel(LogLevel.FINER);
		underTest.setResultLevel(ResultLevel.SUMMARY);
		assertNotEmpty(underTest);
	}

	private void assertNotEmpty(PDFAValidationOptionSpecImpl pdfaValidationOptionSpecImpl) {
		com.adobe.fd.assembler.client.PDFAValidationOptionSpec adobePDFAValidationOptionSpec = AdobeAssemblerServiceAdapter.toAdobePDFAValidationOptionSpec(pdfaValidationOptionSpecImpl);

		assertAll(
				()->assertNotEquals(emptyAdobePDFAValidationOptionSpec.getCompliance(), adobePDFAValidationOptionSpec.getCompliance()),
				()->assertNotEquals(emptyAdobePDFAValidationOptionSpec.getLogLevel(), adobePDFAValidationOptionSpec.getLogLevel()),
				()->assertNotEquals(emptyAdobePDFAValidationOptionSpec.getResultLevel(), adobePDFAValidationOptionSpec.getResultLevel()),
				()->assertNotEquals(emptyAdobePDFAValidationOptionSpec.isAllowCertificationSignatures(), adobePDFAValidationOptionSpec.isAllowCertificationSignatures()),
				()->assertNotEquals(emptyAdobePDFAValidationOptionSpec.isIgnoreUnusedResource(), adobePDFAValidationOptionSpec.isIgnoreUnusedResource())
				);
	}
}
