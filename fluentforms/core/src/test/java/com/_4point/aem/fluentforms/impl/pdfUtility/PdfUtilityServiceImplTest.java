package com._4point.aem.fluentforms.impl.pdfUtility;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService.PdfUtilityException;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesOptionSpec;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesResult;
import com.adobe.fd.pdfutility.services.client.RedactionOptionSpec;
import com.adobe.fd.pdfutility.services.client.RedactionResult;
import com.adobe.fd.pdfutility.services.client.SanitizationResult;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class PdfUtilityServiceImplTest {

	@Mock
	TraditionalPdfUtilityService adobePdfUtilityService;
	
	private PdfUtilityService underTest;
	
	@BeforeEach
	void setUp() throws Exception {
		underTest = new PdfUtilityServiceImpl(adobePdfUtilityService);
	}

	@Test
	void testCloneDocument() throws Exception {
		MockPdfUtilityService svc = setupMocks(MockPdfUtilityService::setupForClone);
		Document inputDoc = Mockito.mock(Document.class);
		
		Document result = underTest.clone(inputDoc);
		
		// Verify the results
		assertSame(svc.getInputDoc(), inputDoc);
		assertSame(svc.getResultDoc(), result);
	}

	@Test
	void testCloneDocument_null() {
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.clone(null));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("doc"),containsString("parameter cannot be null")));
	}

	@Test
	void testConvertPDFtoXDP() throws Exception {
		MockPdfUtilityService svc = setupMocks(MockPdfUtilityService::setupForConvertPDFtoXDP);
		Document inputDoc = Mockito.mock(Document.class);
		
		Document result = underTest.convertPDFtoXDP(inputDoc);
		
		// Verify the results
		assertSame(svc.getInputDoc(), inputDoc);
		assertSame(svc.getResultDoc(), result);
	}

	@Test
	void testConvertPDFtoXDP_null() throws Exception {
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.convertPDFtoXDP(null));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("doc"),containsString("parameter cannot be null")));
	}

	@Test
	void testGetPDFProperties() throws Exception {
		MockPdfUtilityService svc = setupMocks(MockPdfUtilityService::setupForGetPdfProperties);
		PDFPropertiesOptionSpec pdfPropertiesSpec = Mockito.mock(PDFPropertiesOptionSpec.class);
		Document inputDoc = Mockito.mock(Document.class);

		PDFPropertiesResult result = underTest.getPDFProperties(inputDoc, pdfPropertiesSpec);

		// Verify the results
		assertSame(svc.getInputDoc(), inputDoc);
		assertSame(svc.getPdfPropertiesOptions(), pdfPropertiesSpec);
		assertSame(svc.getPdfPropertiesResult(), result);
	}

	@Test
	void testGetPDFProperties_nullDoc() throws Exception {
		PDFPropertiesOptionSpec pdfPropertiesSpec = Mockito.mock(PDFPropertiesOptionSpec.class);
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.getPDFProperties(null, pdfPropertiesSpec));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("doc"),containsString("parameter cannot be null")));
	}

	@Test
	void testGetPDFProperties_nullSpec() throws Exception {
		Document inputDoc = Mockito.mock(Document.class);
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.getPDFProperties(inputDoc, null));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("pdfPropOptionsSpec"),containsString("parameter cannot be null")));
	}

	@Test
	void testMulticlone() throws Exception {
		MockPdfUtilityService svc = setupMocks(MockPdfUtilityService::setupForMulticlone);
		Document inputDoc = Mockito.mock(Document.class);
		int numClones = 2;
		List<Document> result = underTest.multiclone(inputDoc, numClones);
		assertSame(svc.getInputDoc(), inputDoc);
		assertSame(svc.getInputNumClones(), numClones);
		assertEquals(1, result.size());
		assertSame(svc.getResultDoc(), result.get(0));
	}

	void testMulticlone_null() throws Exception {
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.multiclone(null, 2));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("doc"),containsString("parameter cannot be null")));
	}

	@Test
	void testRedact() throws Exception {
		MockPdfUtilityService svc = setupMocks(MockPdfUtilityService::setupForRedact);
		Document inputDoc = Mockito.mock(Document.class);
		RedactionOptionSpec redactSpec = Mockito.mock(RedactionOptionSpec.class);
		RedactionResult result = underTest.redact(inputDoc, redactSpec);
		assertSame(svc.getInputDoc(), inputDoc);
		assertSame(svc.getRedactionOptions(), redactSpec);
		assertSame(svc.getRedactionResult(), result);
	}

	@Test
	void testRedact_nullDoc() throws Exception {
		RedactionOptionSpec redactSpec = Mockito.mock(RedactionOptionSpec.class);
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.redact(null, redactSpec));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("doc"),containsString("parameter cannot be null")));
	}

	@Test
	void testRedact_nullSpec() throws Exception {
		Document inputDoc = Mockito.mock(Document.class);
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.redact(inputDoc, null));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("redactOptionsSpec"),containsString("parameter cannot be null")));
	}

	@Test
	void testSanitize() throws Exception {
		MockPdfUtilityService svc = setupMocks(MockPdfUtilityService::setupForSanitize);
		Document inputDoc = Mockito.mock(Document.class);

		SanitizationResult result = underTest.sanitize(inputDoc);
		
		// Verify the results
		assertSame(svc.getInputDoc(), inputDoc);
		assertSame(svc.getSanitizationResult(), result);
	}

	@Test
	void testSanitize_null() throws Exception {
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.sanitize(null));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("doc"),containsString("parameter cannot be null")));
	}

	/**
	 * This class encapsulates all the mocking related to mocking the PdfUtilityService
	 *
	 */
	private class MockPdfUtilityService {
		// Can't find a way to use Mockito annotations within an inner class, so we have to create them the old fashioned way.
		private final Document resultDoc = Mockito.mock(Document.class);
		private final RedactionResult redactionResult = Mockito.mock(RedactionResult.class);
		private final SanitizationResult sanitizationResult = Mockito.mock(SanitizationResult.class);
		private final PDFPropertiesResult pdfPropertiesResult = Mockito.mock(PDFPropertiesResult.class);
		private final ArgumentCaptor<Document> inputDoc = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<Integer> inputNumClones = ArgumentCaptor.forClass(Integer.class);
		private final ArgumentCaptor<RedactionOptionSpec> redactionOptions = ArgumentCaptor.forClass(RedactionOptionSpec.class);
		private final ArgumentCaptor<PDFPropertiesOptionSpec> pdfPropertiesOptions = ArgumentCaptor.forClass(PDFPropertiesOptionSpec.class);

		private void setupForClone() throws PdfUtilityException {
			Mockito.lenient().when(adobePdfUtilityService.clone(inputDoc.capture())).thenReturn(resultDoc);
		}

		private void setupForConvertPDFtoXDP() throws PdfUtilityException {
			Mockito.lenient().when(adobePdfUtilityService.convertPDFtoXDP(inputDoc.capture())).thenReturn(resultDoc);
		}

		private void setupForMulticlone() throws PdfUtilityException {
			Mockito.lenient().when(adobePdfUtilityService.multiclone(inputDoc.capture(), inputNumClones.capture())).thenReturn(Collections.singletonList(resultDoc));
		}

		private void setupForRedact() throws PdfUtilityException {
			Mockito.lenient().when(adobePdfUtilityService.redact(inputDoc.capture(), redactionOptions.capture())).thenReturn(redactionResult);
		}

		private void setupForSanitize() throws PdfUtilityException {
			Mockito.lenient().when(adobePdfUtilityService.sanitize(inputDoc.capture())).thenReturn(sanitizationResult);
		}

		private void setupForGetPdfProperties() throws PdfUtilityException {
			Mockito.lenient().when(adobePdfUtilityService.getPDFProperties(inputDoc.capture(), pdfPropertiesOptions.capture())).thenReturn(pdfPropertiesResult);
		}

		private Document getResultDoc() {
			return resultDoc;
		}

		private Document getInputDoc() {
			return inputDoc.getValue();
		}

		private RedactionResult getRedactionResult() {
			return redactionResult;
		}

		private SanitizationResult getSanitizationResult() {
			return sanitizationResult;
		}

		private PDFPropertiesResult getPdfPropertiesResult() {
			return pdfPropertiesResult;
		}

		private Integer getInputNumClones() {
			return inputNumClones.getValue();
		}

		private RedactionOptionSpec getRedactionOptions() {
			return redactionOptions.getValue();
		}

		private PDFPropertiesOptionSpec getPdfPropertiesOptions() {
			return pdfPropertiesOptions.getValue();
		}
	}
	
    @FunctionalInterface
    public interface Consumer_WithExceptions<T, E extends Exception> {
        void accept(T t) throws E;
    }

    MockPdfUtilityService setupMocks(Consumer_WithExceptions<MockPdfUtilityService, PdfUtilityException> consumer) throws PdfUtilityException {
		final MockPdfUtilityService mock = new MockPdfUtilityService();
		consumer.accept(mock);
		return mock;
	}
}
