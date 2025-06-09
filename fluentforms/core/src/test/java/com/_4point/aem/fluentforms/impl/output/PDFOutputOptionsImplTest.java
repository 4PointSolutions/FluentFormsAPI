package com._4point.aem.fluentforms.impl.output;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.Xci;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.impl.XciImpl;
import com.adobe.fd.output.api.AcrobatVersion;


class PDFOutputOptionsImplTest {

	private final static com.adobe.fd.output.api.PDFOutputOptions emptyPDFOutputOptions = new com.adobe.fd.output.api.PDFOutputOptions();
	private final PDFOutputOptionsImpl underTest = new PDFOutputOptionsImpl();

	@Test
	@DisplayName("Make sure that if nothing was initialized, then the resulting options are the same as an empty options object.")
	void testToAdobePDFOutputOptions_NoChanges() {
		
		assertEmpty(underTest, null);
	}

	private static void assertEmpty(PDFOutputOptionsImpl pdfOutputOptions, String contentRoot) {
		com.adobe.fd.output.api.PDFOutputOptions adobePDFOutputOptions = AdobeOutputServiceAdapter.toAdobePDFOutputOptions(pdfOutputOptions);
		assertEquals(emptyPDFOutputOptions.getAcrobatVersion(), adobePDFOutputOptions.getAcrobatVersion());
		assertEquals(contentRoot, adobePDFOutputOptions.getContentRoot());	// We modify the content root, so we expect it to be different than the empty version (which would be null).
		assertEquals(emptyPDFOutputOptions.getDebugDir(), adobePDFOutputOptions.getDebugDir());
		assertEquals(emptyPDFOutputOptions.getEmbedFonts(), adobePDFOutputOptions.getEmbedFonts());
		assertEquals(emptyPDFOutputOptions.getLinearizedPDF(), adobePDFOutputOptions.getLinearizedPDF());
		assertEquals(emptyPDFOutputOptions.getLocale(), adobePDFOutputOptions.getLocale());
		assertEquals(emptyPDFOutputOptions.getRetainPDFFormState(), adobePDFOutputOptions.getRetainPDFFormState());
		assertEquals(emptyPDFOutputOptions.getRetainUnsignedSignatureFields(), adobePDFOutputOptions.getRetainUnsignedSignatureFields());
		assertEquals(emptyPDFOutputOptions.getTaggedPDF(), adobePDFOutputOptions.getTaggedPDF());
		assertEquals(emptyPDFOutputOptions.getXci(), adobePDFOutputOptions.getXci());
	}
	
	@Test
	@DisplayName("Make sure that if everything was initialized, then the resulting options are the not same as an empty options object.")
	void testToAdobePDFOutputOptions_AllChanges() {

		underTest.setAcrobatVersion(AcrobatVersion.Acrobat_10_1);
		underTest.setContentRoot(Paths.get("foo", "bar"));
		underTest.setDebugDir(Paths.get("bar", "foo"));
		underTest.setEmbedFonts(true);
		underTest.setLinearizedPDF(true);
		underTest.setLocale(Locale.CANADA_FRENCH);
		underTest.setRetainPDFFormState(true);
		underTest.setRetainUnsignedSignatureFields(true);
		underTest.setTaggedPDF(true);
		underTest.setXci(
				new XciImpl.XciBuilderImpl().pdf()
											.embedFonts(true)
											.buildDestination()
							.build());

		assertNotEmpty(underTest);
	}

	/* package */ static void assertNotEmpty(PDFOutputOptions pdfOutputOptions) {
		com.adobe.fd.output.api.PDFOutputOptions adobePDFOutputOptions = AdobeOutputServiceAdapter.toAdobePDFOutputOptions(pdfOutputOptions);
		assertNotEquals(emptyPDFOutputOptions.getAcrobatVersion(), adobePDFOutputOptions.getAcrobatVersion());
		assertNotEquals(emptyPDFOutputOptions.getContentRoot(), adobePDFOutputOptions.getContentRoot());
		assertNotEquals(emptyPDFOutputOptions.getDebugDir(), adobePDFOutputOptions.getDebugDir());
		assertNotEquals(emptyPDFOutputOptions.getEmbedFonts(), adobePDFOutputOptions.getEmbedFonts());
		assertNotEquals(emptyPDFOutputOptions.getLinearizedPDF(), adobePDFOutputOptions.getLinearizedPDF());
		assertNotEquals(emptyPDFOutputOptions.getLocale(), adobePDFOutputOptions.getLocale());
		assertNotEquals(emptyPDFOutputOptions.getRetainPDFFormState(), adobePDFOutputOptions.getRetainPDFFormState());
		assertNotEquals(emptyPDFOutputOptions.getRetainUnsignedSignatureFields(), adobePDFOutputOptions.getRetainUnsignedSignatureFields());
		assertNotEquals(emptyPDFOutputOptions.getTaggedPDF(), adobePDFOutputOptions.getTaggedPDF());
		// We can't test the creation of XCI document because that would require a real Adobe implementation to be available,
		// so we just make sure that the XCI object is not null.
		assertNotNull(pdfOutputOptions.getXci());
	}

}
