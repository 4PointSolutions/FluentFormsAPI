package com._4point.aem.fluentforms.impl.convertPdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ConvertPdfServiceException;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpec;
import com.adobe.fd.cpdf.api.enumeration.CMYKPolicy;
import com.adobe.fd.cpdf.api.enumeration.ColorCompression;
import com.adobe.fd.cpdf.api.enumeration.ColorSpace;
import com.adobe.fd.cpdf.api.enumeration.GrayScaleCompression;
import com.adobe.fd.cpdf.api.enumeration.GrayScalePolicy;
import com.adobe.fd.cpdf.api.enumeration.ImageConvertFormat;
import com.adobe.fd.cpdf.api.enumeration.Interlace;
import com.adobe.fd.cpdf.api.enumeration.JPEGFormat;
import com.adobe.fd.cpdf.api.enumeration.MonochromeCompression;
import com.adobe.fd.cpdf.api.enumeration.PNGFilter;
import com.adobe.fd.cpdf.api.enumeration.RGBPolicy;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
public class ConvertPdfServiceImplTest {

	@Mock
	private TraditionalConvertPdfService adobeConvertPdfService;
	
	private ConvertPdfService underTest;
	

	@BeforeEach
	void setUp() throws Exception {
		underTest = new ConvertPdfServiceImpl(adobeConvertPdfService);
	}

	@Test
	@DisplayName("Test ToImage(Document, ToImageOptionsSpec) Happy Path.")
	void testToImage() throws Exception {
		MockConvertPdfToImageService svc = new MockConvertPdfToImageService();
		Document pdfDoc = Mockito.mock(Document.class);
		ToImageOptionsSpec imageOptions = Mockito.mock(ToImageOptionsSpec.class);
		List<Document> result = underTest.toImage(pdfDoc, imageOptions);
		
		// Verify that all the results are correct.
		assertEquals(pdfDoc, svc.getInputDoc(), "Expected the PDF passed to AEM would match the PDF used.");
		assertSame(imageOptions, svc.getImageOptions(), "Expected the toImageOptionsSpec passed to AEM would match the toImageOptionsSpec used.");
		assertSame(result, svc.getResult(), "Expected the List<Document> returned by AEM would match the List<Document> result.");
	}

	@Test
	@DisplayName("Test ToImage(Document, ToImageOptionsSpec) with null arguments.")
	void testToImage_nullArguments() throws Exception {
		Document pdfDoc = Mockito.mock(Document.class);
		ToImageOptionsSpec imageOptions = Mockito.mock(ToImageOptionsSpec.class);
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.toImage(null, imageOptions));
		assertTrue(ex1.getMessage().contains("inPdfDoc cannot be null"));
		
		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.toImage(pdfDoc, null));
		assertTrue(ex2.getMessage().contains("toImageOptionsSpec cannot be null"));
	}

	@Test
	@DisplayName("Test ToImage(Document, ToImageOptionsSpec) throws ConvertPdfServiceException.")
	void testToImage_ConvertPdfServiceExceptionThrown() throws Exception {
		Mockito.when(adobeConvertPdfService.toImage(Mockito.any(Document.class), Mockito.any())).thenThrow(ConvertPdfServiceException.class);
		Document pdfDoc = Mockito.mock(Document.class);
		ToImageOptionsSpec imageOptions = Mockito.mock(ToImageOptionsSpec.class);
		
		assertThrows(ConvertPdfServiceException.class, ()->underTest.toImage(pdfDoc, imageOptions));
	}

	@Test
	@DisplayName("Test ToImage(Document, ToImageOptionsSpec) creating ToImageOptionsSpec values.")
	void testConvertPdfToImage() throws Exception {
		MockConvertPdfToImageService svc = new MockConvertPdfToImageService();
		Document pdfDoc = Mockito.mock(Document.class);
		List<Document> result = underTest.toImage()
									.setCmykPolicy(CMYKPolicy.Off)
									.setColorCompression(ColorCompression.None)
									.setColorSpace(ColorSpace.GrayScale)
									.setFilter(PNGFilter.None)
									.setFormat(JPEGFormat.BaselineStandard)
									.setGrayScaleCompression(GrayScaleCompression.Lossless)
									.setGrayScalePolicy(GrayScalePolicy.Off)
									.setImageConvertFormat(ImageConvertFormat.JPEG)
									.setIncludeComments(false)
									.setInterlace(Interlace.None)
									.setMonochrome(MonochromeCompression.LZW)
									.setMultiPageTiff(false)
									.setResolution("300")
									.setRgbPolicy(RGBPolicy.Off)
									.setRowsPerStrip(1)
									.setUseLegacyImageSizeBehavior(false)
									.executeOn(pdfDoc);
		
		assertEquals(pdfDoc, svc.getInputDoc(), "Expected the PDF passed to AEM would match the PDF used.");
		assertSame(result, svc.getResult(), "Expected the List<Document> returned by AEM would match the List<Document> result.");
	}

	@Test
	@DisplayName("Test ToPS(Document, ToPSOptionsSpec) Happy Path.")
	void testToPS() throws Exception {
		MockConvertPdfToPSService svc = new MockConvertPdfToPSService();
		Document pdfDoc = Mockito.mock(Document.class);
		ToPSOptionsSpec psOptions = Mockito.mock(ToPSOptionsSpec.class);
		Document result = underTest.toPS(pdfDoc, psOptions);
		
		// Verify that all the results are correct.
		assertEquals(pdfDoc, svc.getInputDoc(), "Expected the PDF passed to AEM would match the PDF used.");
		assertSame(psOptions, svc.getPSOptions(), "Expected the toPSOptionsSpec passed to AEM would match the toPSOptionsSpec used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test ToPS(Document, ToPSOptionsSpec) with null arguments.")
	void testToPS_nullArguments() throws Exception {
		Document pdfDoc = Mockito.mock(Document.class);
		ToPSOptionsSpec psOptions = Mockito.mock(ToPSOptionsSpec.class);
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.toPS(null, psOptions));
		assertTrue(ex1.getMessage().contains("inPdfDoc cannot be null"));
		
		MockConvertPdfToPSService svc = new MockConvertPdfToPSService();
		Document result = underTest.toPS(pdfDoc, null);
		assertEquals(pdfDoc, svc.getInputDoc(), "Expected the PDF passed to AEM would match the PDF used.");
		assertNull(svc.getPSOptions());
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test ToPS(Document, ToImageOptionsSpec) throws ConvertPdfServiceException.")
	void testToPS_ConvertPdfServiceExceptionThrown() throws Exception {
		Mockito.when(adobeConvertPdfService.toPS(Mockito.any(Document.class), Mockito.any())).thenThrow(ConvertPdfServiceException.class);
		Document pdfDoc = Mockito.mock(Document.class);
		ToPSOptionsSpec psOptions = Mockito.mock(ToPSOptionsSpec.class);
		
		assertThrows(ConvertPdfServiceException.class, ()->underTest.toPS(pdfDoc, psOptions));
	}
	
	private class MockConvertPdfToImageService {
		private final List<Document> result = Collections.singletonList(Mockito.mock(Document.class));
		private final ArgumentCaptor<Document> inputDoc = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<ToImageOptionsSpec> imageOptions = ArgumentCaptor.forClass(ToImageOptionsSpec.class);
		
		protected MockConvertPdfToImageService() throws ConvertPdfServiceException {
			super();
			Mockito.when(adobeConvertPdfService.toImage(inputDoc.capture(), imageOptions.capture())).thenReturn(result);
		}

		protected List<Document> getResult() {
			return result;
		}

		protected Document getInputDoc() {
			return inputDoc.getValue();
		}

		protected ToImageOptionsSpec getImageOptions() {
			return imageOptions.getValue();
		}
	}
		
	private class MockConvertPdfToPSService {
		private final Document result = Mockito.mock(Document.class);
		private final ArgumentCaptor<Document> inputDoc = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<ToPSOptionsSpec> psOptions = ArgumentCaptor.forClass(ToPSOptionsSpec.class);
		
		protected MockConvertPdfToPSService() throws ConvertPdfServiceException {
			super();
			// These are "lenient" because we only expect one or the other to be called.
			Mockito.when(adobeConvertPdfService.toPS(inputDoc.capture(), psOptions.capture())).thenReturn(result);
		}

		protected Document getResult() {
			return result;
		}

		protected Document getInputDoc() {
			return inputDoc.getValue();
		}

		protected ToPSOptionsSpec getPSOptions() {
			return psOptions.getValue();
		}
	}
}
