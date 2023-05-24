package com._4point.aem.fluentforms.impl.forms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;
import com._4point.aem.fluentforms.impl.forms.AdobeFormsServiceAdapter;
import com._4point.aem.fluentforms.impl.forms.PDFFormRenderOptionsImpl;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.RenderAtClient;

class PDFFormRenderOptionsImplTest {
	
	private static final String EXPECTED_SUBMIT_URL = "http://example.com";
	private final static com.adobe.fd.forms.api.PDFFormRenderOptions emptyPDFFormRenderOptions = new com.adobe.fd.forms.api.PDFFormRenderOptions();
	private final PDFFormRenderOptionsImpl underTest = new PDFFormRenderOptionsImpl();
	
	@Test
	@DisplayName("Make sure that if nothing was initialized, then the resulting options are the same as an empty options object.")
	void testToAdobePDFFormRenderOptions_NoChanges() {
		
		assertEmpty(underTest, null);
	}

	public static void assertEmpty(PDFFormRenderOptions pdfFormRenderOptions, String contentRoot) {
		com.adobe.fd.forms.api.PDFFormRenderOptions adobePDFFormRenderOptions = AdobeFormsServiceAdapter.toAdobePDFFormRenderOptions(pdfFormRenderOptions);
		assertEquals(emptyPDFFormRenderOptions.getAcrobatVersion(), adobePDFFormRenderOptions.getAcrobatVersion());
		assertEquals(emptyPDFFormRenderOptions.getCacheStrategy(), adobePDFFormRenderOptions.getCacheStrategy());
		assertEquals(contentRoot, adobePDFFormRenderOptions.getContentRoot());	// We modify the content root, so we expect it to be different than the empty version (which would be null).
		assertEquals(emptyPDFFormRenderOptions.getDebugDir(), adobePDFFormRenderOptions.getDebugDir());
		assertEquals(emptyPDFFormRenderOptions.getEmbedFonts(), adobePDFFormRenderOptions.getEmbedFonts());
		assertEquals(emptyPDFFormRenderOptions.getLocale(), adobePDFFormRenderOptions.getLocale());
		assertEquals(emptyPDFFormRenderOptions.getRenderAtClient(), adobePDFFormRenderOptions.getRenderAtClient());
		assertEquals(emptyPDFFormRenderOptions.getSubmitUrls(), adobePDFFormRenderOptions.getSubmitUrls());
		assertEquals(emptyPDFFormRenderOptions.getTaggedPDF(), adobePDFFormRenderOptions.getTaggedPDF());
		assertEquals(emptyPDFFormRenderOptions.getXci(), adobePDFFormRenderOptions.getXci());
	}

	@Test
	@DisplayName("Make sure that if everything was initialized, then the resulting options are not the same as an empty options object.")
	void testToAdobePDFFormRenderOptions_AllChanges() throws MalformedURLException {
		
		underTest.setAcrobatVersion(AcrobatVersion.Acrobat_10_1);
		underTest.setCacheStrategy(CacheStrategy.NONE);
		underTest.setContentRoot(Paths.get("foo", "bar"));
		underTest.setDebugDir(Paths.get("bar", "foo"));
		underTest.setEmbedFonts(true);
		underTest.setLocale(Locale.CANADA_FRENCH);
		underTest.setRenderAtClient(RenderAtClient.NO);
		underTest.setSubmitUrl(new URL(EXPECTED_SUBMIT_URL));
		underTest.setTaggedPDF(true);
		// Omit the creation of XCI document because that would require a real Adobe implementation to be available.
//		underTest.setXci(new MockDocumentFactory().create(new byte[0]));
		
		
		assertNotEmpty(underTest);
//		assertNotEquals(emptyPDFFormRenderOptions.getXci(), adobePDFFormRenderOptions.getXci());
	}

	/* package */ static void assertNotEmpty(PDFFormRenderOptions pdfFormRenderOptions) {
		com.adobe.fd.forms.api.PDFFormRenderOptions adobePDFFormRenderOptions = AdobeFormsServiceAdapter.toAdobePDFFormRenderOptions(pdfFormRenderOptions);
		assertNotEquals(emptyPDFFormRenderOptions.getAcrobatVersion(), adobePDFFormRenderOptions.getAcrobatVersion());
		assertNotEquals(emptyPDFFormRenderOptions.getCacheStrategy(), adobePDFFormRenderOptions.getCacheStrategy());
		assertNotEquals(emptyPDFFormRenderOptions.getContentRoot(), adobePDFFormRenderOptions.getContentRoot());
		assertNotEquals(emptyPDFFormRenderOptions.getDebugDir(), adobePDFFormRenderOptions.getDebugDir());
		assertNotEquals(emptyPDFFormRenderOptions.getEmbedFonts(), adobePDFFormRenderOptions.getEmbedFonts());
		assertNotEquals(emptyPDFFormRenderOptions.getLocale(), adobePDFFormRenderOptions.getLocale());
		assertNotEquals(emptyPDFFormRenderOptions.getRenderAtClient(), adobePDFFormRenderOptions.getRenderAtClient());
		assertNotEquals(emptyPDFFormRenderOptions.getSubmitUrls(), adobePDFFormRenderOptions.getSubmitUrls());
		assertEquals(EXPECTED_SUBMIT_URL, adobePDFFormRenderOptions.getSubmitUrls().get(0));
		assertNotEquals(emptyPDFFormRenderOptions.getTaggedPDF(), adobePDFFormRenderOptions.getTaggedPDF());
	}

	// TODO: Test PDFFormRenderOptionsSetter default methods here
	//       They are not currently being tested anywhere...
}
