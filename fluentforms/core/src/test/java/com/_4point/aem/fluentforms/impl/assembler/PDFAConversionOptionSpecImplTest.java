package com._4point.aem.fluentforms.impl.assembler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures;

class PDFAConversionOptionSpecImplTest {

	private static final com.adobe.fd.assembler.client.PDFAConversionOptionSpec emptyAdobePDFAConversionOptionSpec = new com.adobe.fd.assembler.client.PDFAConversionOptionSpec();;
	private static final Document DUMMY_DOCUMENT = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;
	
	private final PDFAConversionOptionSpecImpl underTest = new PDFAConversionOptionSpecImpl();
	
	@Test
	@DisplayName("Make sure that if nothing was initialized, then the resulting options are the same as an empty options object.")
	void testEmptyPDFAConversionOptionSpecImpl() {
		assertEmpty(underTest);
	}

	private void assertEmpty(PDFAConversionOptionSpecImpl conversionOptionSpecImpl) {
		com.adobe.fd.assembler.client.PDFAConversionOptionSpec adobePDFAConversionOptionSpec = AdobeAssemblerServiceAdapter.toAdobePDFAConversionOptionSpec(conversionOptionSpecImpl);
		assertAll(
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getColorSpace(), adobePDFAConversionOptionSpec.getColorSpace()),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getCompliance(), adobePDFAConversionOptionSpec.getCompliance()),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getLogLevel(), adobePDFAConversionOptionSpec.getLogLevel()),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getMetadataSchemaExtensions(), adobePDFAConversionOptionSpec.getMetadataSchemaExtensions()),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getOptionalContent(), adobePDFAConversionOptionSpec.getOptionalContent()),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getResultLevel(), adobePDFAConversionOptionSpec.getResultLevel()),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getSignatures(), adobePDFAConversionOptionSpec.getSignatures())
				);
	}
	
	@Test
	@DisplayName("Make sure that if everything was initialized, then the resulting options are the not same as an empty options object.")
	void testToAdobePDFAConversionOptionSpec_AllChanges() {
		underTest.setColorSpace(ColorSpace.JAPAN_COLOR_COATED);
		underTest.setCompliance(Compliance.PDFA_2B);
		underTest.setLogLevel(LogLevel.FINEST);
		underTest.setMetadataSchemaExtensions(Collections.singletonList(DUMMY_DOCUMENT));
		underTest.setOptionalContent(OptionalContent.ALL);
		underTest.setRemoveInvalidXMPProperties(true);
		underTest.setResultLevel(ResultLevel.SUMMARY);
		underTest.setSignatures(Signatures.ARCHIVE_ALWAYS);
		underTest.setVerify(false);
		assertNotEmpty(underTest);
	}
	
	private void assertNotEmpty(PDFAConversionOptionSpecImpl conversionOptionSpecImpl) {
		com.adobe.fd.assembler.client.PDFAConversionOptionSpec adobePDFAConversionOptionSpec = AdobeAssemblerServiceAdapter.toAdobePDFAConversionOptionSpec(conversionOptionSpecImpl);
		assertAll(
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getColorSpace(), adobePDFAConversionOptionSpec.getColorSpace()),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getCompliance(), adobePDFAConversionOptionSpec.getCompliance()),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getLogLevel(), adobePDFAConversionOptionSpec.getLogLevel()),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getMetadataSchemaExtensions(), adobePDFAConversionOptionSpec.getMetadataSchemaExtensions()),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getOptionalContent(), adobePDFAConversionOptionSpec.getOptionalContent()),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getResultLevel(), adobePDFAConversionOptionSpec.getResultLevel()),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getSignatures(), adobePDFAConversionOptionSpec.getSignatures())
				);
	}
}
