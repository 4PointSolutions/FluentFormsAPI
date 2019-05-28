package com._4point.aem.fluentforms.api.forms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.impl.forms.PDFFormRenderOptionsImpl;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;

class PDFFormRenderOptionsImplTest {
	
	private final static com.adobe.fd.forms.api.PDFFormRenderOptions emptyPDFFormRenderOptions = new com.adobe.fd.forms.api.PDFFormRenderOptions();
	private final PDFFormRenderOptionsImpl underTest = new PDFFormRenderOptionsImpl();
	
	@Test
	@DisplayName("Make sure that if nothing was initialized, then the resulting options are the same as an empty options object.")
	void testToAdobePDFFormRenderOptions_NoChanges() {
		
		com.adobe.fd.forms.api.PDFFormRenderOptions adobePDFFormRenderOptions = underTest.toAdobePDFFormRenderOptions();
		
		assertEmpty(adobePDFFormRenderOptions);
	}

	public static void assertEmpty(com.adobe.fd.forms.api.PDFFormRenderOptions adobePDFFormRenderOptions) {
		assertEquals(emptyPDFFormRenderOptions.getAcrobatVersion(), adobePDFFormRenderOptions.getAcrobatVersion());
		assertEquals(emptyPDFFormRenderOptions.getCacheStrategy(), adobePDFFormRenderOptions.getCacheStrategy());
		assertEquals(emptyPDFFormRenderOptions.getContentRoot(), adobePDFFormRenderOptions.getContentRoot());
		assertEquals(emptyPDFFormRenderOptions.getDebugDir(), adobePDFFormRenderOptions.getDebugDir());
		assertEquals(emptyPDFFormRenderOptions.getLocale(), adobePDFFormRenderOptions.getLocale());
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
		underTest.setLocale(Locale.CANADA_FRENCH);
		underTest.setSubmitUrl(new URL("http://example.com"));
		underTest.setTaggedPDF(true);
		// Omit the creation of XCI document because that would require a real Adobe implementation to be available.
//		underTest.setXci(new MockDocumentFactory().create(new byte[0]));
		
		com.adobe.fd.forms.api.PDFFormRenderOptions adobePDFFormRenderOptions = underTest.toAdobePDFFormRenderOptions();
		
		assertNotEmpty(adobePDFFormRenderOptions);
//		assertNotEquals(emptyPDFFormRenderOptions.getXci(), adobePDFFormRenderOptions.getXci());
	}

	public static void assertNotEmpty(com.adobe.fd.forms.api.PDFFormRenderOptions adobePDFFormRenderOptions) {
		assertNotEquals(emptyPDFFormRenderOptions.getAcrobatVersion(), adobePDFFormRenderOptions.getAcrobatVersion());
		assertNotEquals(emptyPDFFormRenderOptions.getCacheStrategy(), adobePDFFormRenderOptions.getCacheStrategy());
		assertNotEquals(emptyPDFFormRenderOptions.getContentRoot(), adobePDFFormRenderOptions.getContentRoot());
		assertNotEquals(emptyPDFFormRenderOptions.getDebugDir(), adobePDFFormRenderOptions.getDebugDir());
		assertNotEquals(emptyPDFFormRenderOptions.getLocale(), adobePDFFormRenderOptions.getLocale());
		assertNotEquals(emptyPDFFormRenderOptions.getSubmitUrls(), adobePDFFormRenderOptions.getSubmitUrls());
		assertNotEquals(emptyPDFFormRenderOptions.getTaggedPDF(), adobePDFFormRenderOptions.getTaggedPDF());
	}

	// TODO: Test PDFFormRenderOptionsSetter default methods here
	//       They are not currently being tested anywhere...
}
