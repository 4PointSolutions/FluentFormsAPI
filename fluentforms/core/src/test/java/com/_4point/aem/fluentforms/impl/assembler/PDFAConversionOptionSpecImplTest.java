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
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getColorSpace(), adobePDFAConversionOptionSpec.getColorSpace(), "getColorSpace default is different"),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getCompliance(), adobePDFAConversionOptionSpec.getCompliance(), "getCompliance default is different"),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getLogLevel(), adobePDFAConversionOptionSpec.getLogLevel(), "getLogLevel default is different"),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getMetadataSchemaExtensions(), adobePDFAConversionOptionSpec.getMetadataSchemaExtensions(), "getMetadataSchemaExtensions default is different"),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getOptionalContent(), adobePDFAConversionOptionSpec.getOptionalContent(), "getOptionalContent default is different"),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getResultLevel(), adobePDFAConversionOptionSpec.getResultLevel(), "getResultLevel default is different"),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.getSignatures(), adobePDFAConversionOptionSpec.getSignatures(), "getSignatures default is different"),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.isRemoveInvalidXMPProperties(), adobePDFAConversionOptionSpec.isRemoveInvalidXMPProperties(), "isRemoveInvalidXMPProperties default is different"),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.isRetainPDFFormState(), adobePDFAConversionOptionSpec.isRetainPDFFormState(), "isRetainPDFFormState default is different"),
				()->assertEquals(emptyAdobePDFAConversionOptionSpec.isVerify(), adobePDFAConversionOptionSpec.isVerify(), "isVerify default is different")
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
		underTest.setRemoveInvalidXMPProperties(false);
		underTest.setResultLevel(ResultLevel.SUMMARY);
		underTest.setRetainPDFFormState(true);
		underTest.setSignatures(Signatures.ARCHIVE_ALWAYS);
		underTest.setVerify(false);
		assertNotEmpty(underTest);
	}
	
	private void assertNotEmpty(PDFAConversionOptionSpecImpl conversionOptionSpecImpl) {
		com.adobe.fd.assembler.client.PDFAConversionOptionSpec adobePDFAConversionOptionSpec = AdobeAssemblerServiceAdapter.toAdobePDFAConversionOptionSpec(conversionOptionSpecImpl);
		assertAll(
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getColorSpace(), adobePDFAConversionOptionSpec.getColorSpace(), "getColorSpace default is not being transfered."),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getCompliance(), adobePDFAConversionOptionSpec.getCompliance(), "getCompliance default is not being transfered."),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getLogLevel(), adobePDFAConversionOptionSpec.getLogLevel(), "getLogLevel default is not being transfered."),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getMetadataSchemaExtensions(), adobePDFAConversionOptionSpec.getMetadataSchemaExtensions(), "getMetadataSchemaExtensions default is not being transfered."),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getOptionalContent(), adobePDFAConversionOptionSpec.getOptionalContent(), "getOptionalContent default is not being transfered."),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getResultLevel(), adobePDFAConversionOptionSpec.getResultLevel(), "getResultLevel default is not being transfered."),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.getSignatures(), adobePDFAConversionOptionSpec.getSignatures(), "getSignatures default is not being transfered."),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.isRemoveInvalidXMPProperties(), adobePDFAConversionOptionSpec.isRemoveInvalidXMPProperties(), "isRemoveInvalidXMPProperties default is not being transfered."),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.isRetainPDFFormState(), adobePDFAConversionOptionSpec.isRetainPDFFormState(), "isRetainPDFFormState default is not being transfered."),
				()->assertNotEquals(emptyAdobePDFAConversionOptionSpec.isVerify(), adobePDFAConversionOptionSpec.isVerify(), "isVerify default is not being transfered.")
				);
	}
}
