package com._4point.aem.docservices.rest_services.client.convertPdf;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload.Builder;
import com._4point.aem.docservices.rest_services.client.RestClient.Response;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl.TriFunction;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ConvertPdfServiceException;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpec;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.testing.matchers.javalang.ExceptionMatchers;
import com.adobe.fd.cpdf.api.enumeration.CMYKPolicy;
import com.adobe.fd.cpdf.api.enumeration.Color;
import com.adobe.fd.cpdf.api.enumeration.ColorCompression;
import com.adobe.fd.cpdf.api.enumeration.ColorSpace;
import com.adobe.fd.cpdf.api.enumeration.FontInclusion;
import com.adobe.fd.cpdf.api.enumeration.GrayScaleCompression;
import com.adobe.fd.cpdf.api.enumeration.GrayScalePolicy;
import com.adobe.fd.cpdf.api.enumeration.ImageConvertFormat;
import com.adobe.fd.cpdf.api.enumeration.Interlace;
import com.adobe.fd.cpdf.api.enumeration.JPEGFormat;
import com.adobe.fd.cpdf.api.enumeration.LineWeight;
import com.adobe.fd.cpdf.api.enumeration.MonochromeCompression;
import com.adobe.fd.cpdf.api.enumeration.PNGFilter;
import com.adobe.fd.cpdf.api.enumeration.PSLevel;
import com.adobe.fd.cpdf.api.enumeration.PageSize;
import com.adobe.fd.cpdf.api.enumeration.RGBPolicy;
import com.adobe.fd.cpdf.api.enumeration.Style;

@ExtendWith(MockitoExtension.class)
public class RestServicesConvertPdfServiceAdapterTest {

	private final static Document DUMMY_PDF = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;

	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

	private static final String PDF_PARAM = "inPdfDoc";
	private static final String CMYK_POLICY_PARAM = "toImageOptionsSpec.cmykPolicy";
	private static final String COLOR_COMPRESSION_PARAM = "toImageOptionsSpec.colorCompression";
	private static final String COLOR_SPACE_PARAM = "toImageOptionsSpec.colorSpace";
	private static final String PNG_FILTER_PARAM = "toImageOptionsSpec.filter";
	private static final String JPEG_FORMAT_PARAM = "toImageOptionsSpec.format";
	private static final String GRAY_SCALE_COMPRESSION_PARAM = "toImageOptionsSpec.grayScaleCompression";
	private static final String GRAY_SCALE_POLICY_PARAM = "toImageOptionsSpec.grayScalePolicy";
	private static final String IMAGE_CONVERT_FORMAT_PARAM = "toImageOptionsSpec.imageConvertFormat";
	private static final String IMAGE_SIZE_HEIGHT_PARAM = "toImageOptionsSpec.imageSizeHeight";
	private static final String IMAGE_SIZE_WIDTH_PARAM = "toImageOptionsSpec.imageSizeWidth";
	private static final String IMAGE_INCLUDE_COMMENTS_PARAM = "toImageOptionsSpec.includeComments";
	private static final String INTERLACE_PARAM = "toImageOptionsSpec.interlace";
	private static final String MONOCHROME_COMPRESSION_PARAM = "toImageOptionsSpec.monochrome";
	private static final String MULTI_PAGE_TIFF_PARAM = "toImageOptionsSpec.multiPageTiff";
	private static final String IMAGE_PAGE_RANGE_PARAM = "toImageOptionsSpec.pageRange";
	private static final String RESOLUTION_PARAM = "toImageOptionsSpec.resolution";
	private static final String RGB_POLICY_PARAM = "toImageOptionsSpec.rgbPolicy";
	private static final String ROWS_PER_STRIP_PARAM = "toImageOptionsSpec.rowsPerStrip";
	private static final String TILE_SIZE_PARAM = "toImageOptionsSpec.tileSize";
	private static final String USE_LEGACY_IMAGE_SIZE_BEHAVIOR_PARAM = "toImageOptionsSpec.useLegacyImageSizeBehavior";
	private static final String COLOR_PARAM = "toPSOptionsSpec.color";
	private static final String FONT_INCLUSION_PARAM = "toPSOptionsSpec.fontInclusion";
	private static final String LINE_WEIGHT_PARAM = "toPSOptionsSpec.lineWeight";
	private static final String PS_PAGE_RANGE_PARAM = "toPSOptionsSpec.pageRange";
	private static final String PAGE_SIZE_PARAM = "toPSOptionsSpec.pageSize";
	private static final String PAGE_SIZE_HEIGHT_PARAM = "toPSOptionsSpec.pageSizeHeight";
	private static final String PAGE_SIZE_WIDTH_PARAM = "toPSOptionsSpec.pageSizeWidth";
	private static final String PS_LEVEL_PARAM = "toPSOptionsSpec.psLevel";
	private static final String STYLE_PARAM = "toPSOptionsSpec.style";
	private static final String ALLOW_BINARY_CONTENT_PARAM = "toPSOptionsSpec.allowBinaryContent";
	private static final String BLEED_MARKS_PARAM = "toPSOptionsSpec.bleedMarks";
	private static final String COLOR_BARS_PARAM = "toPSOptionsSpec.colorBars";
	private static final String CONVERT_TRUE_TYPE_TO_TYPE1_PARAM = "toPSOptionsSpec.convertTrueTypeToType1";
	private static final String EMIT_CID_FONT_TYPE2_PARAM = "toPSOptionsSpec.emitCIDFontType2";
	private static final String EMIT_PS_FORM_OBJECTS_PARAM = "toPSOptionsSpec.emitPSFormsObjects";
	private static final String EXPAND_TO_FIT_PARAM = "toPSOptionsSpec.expandToFit";
	private static final String PS_INCLUDE_COMMENTS_PARAM = "toPSOptionsSpec.includeComments";
	private static final String LEGACY_TO_SIMPLE_PS_FLAG_PARAM = "toPSOptionsSpec.legacyToSimplePSFlag";
	private static final String PAGE_INFORMATION_PARAM = "toPSOptionsSpec.pageInformation";
	private static final String REGISTRATION_MARKS_PARAM = "toPSOptionsSpec.registrationMarks";
	private static final String REVERSE_PARAM = "toPSOptionsSpec.reverse";
	private static final String ROTATE_AND_CENTER_PARAM = "toPSOptionsSpec.rotateAndCenter";
	private static final String SHRINK_TO_FIT_PARAM = "toPSOptionsSpec.shrinkToFit";
	private static final String TRIM_MARKS_PARAM = "toPSOptionsSpec.trimMarks";
	private static final String USE_MAX_JPEG_IMAGE_RESOLUTION_PARAM = "toPSOptionsSpec.useMaxJPEGImageResolution";

//
////	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
//	private static final MediaType APPLICATION_JPEG = new MediaType("image", "jpeg");
////	private static final MediaType APPLICATION_JPEG2K = new MediaType("image", "jp2");
////	private static final MediaType APPLICATION_PNG = new MediaType("image", "png");
////	private static final MediaType APPLICATION_TIFF = new MediaType("image", "tiff");
//	private static final MediaType APPLICATION_PS = new MediaType("application", "postscript");

	@Mock(stubOnly = true) TriFunction<AemConfig, String, Supplier<String>, RestClient> mockClientFactory;
	@Mock(stubOnly = true) RestClient mockClient;
	@Mock(stubOnly = true) MultipartPayload mockPayload;
	@Mock(stubOnly = true) MultipartPayload.Builder mockPayloadBuilder;
	@Mock(stubOnly = true) Response mockResponse;

	@Captor ArgumentCaptor<AemConfig> aemConfig;
	@Captor ArgumentCaptor<String> servicePath;
	@Captor ArgumentCaptor<InputStream> postBodyBytes;
	@Captor ArgumentCaptor<ContentType> acceptableContentType;
	@Captor ArgumentCaptor<Supplier<String>> correlationIdFn;

	
	@BeforeEach
	void setUp() throws Exception {
		when(mockClientFactory.apply(aemConfig.capture(), servicePath.capture(), correlationIdFn.capture())).thenReturn(mockClient);
	}

	@Test
	void testToImage_NullArguments() throws Exception {
		RestServicesConvertPdfServiceAdapter underTest = RestServicesConvertPdfServiceAdapter.builder(mockClientFactory).build();
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.toImage((Document)null, null));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("inPdfDoc"));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("cannot be null"));
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.toImage(DUMMY_PDF, null));
		assertThat(ex3.getMessage(), containsStringIgnoringCase("toImageOptionsSpec"));
		assertThat(ex3.getMessage(), containsStringIgnoringCase("cannot be null"));
	}

	@Test
	void testToPS_NullArguments() throws Exception {
		RestServicesConvertPdfServiceAdapter underTest = RestServicesConvertPdfServiceAdapter.builder(mockClientFactory).build();
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.toPS((Document)null, null));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("inPdfDoc"));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("cannot be null"));
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.toPS(DUMMY_PDF, null));
		assertThat(ex3.getMessage(), containsStringIgnoringCase("toPSOptionsSpec"));
		assertThat(ex3.getMessage(), containsStringIgnoringCase("cannot be null"));
	}

	@Test
	void testToImage_HappyPath() throws Exception {
		byte[] responseData = "response Document Data".getBytes();
		setupMocks(setupMockResponse(responseData, ContentType.IMAGE_JPEG));
			
		RestServicesConvertPdfServiceAdapter underTest = createAdapter(mockClientFactory);
		
		ToImageOptionsSpec toImageOptionsSpec = Mockito.mock(ToImageOptionsSpec.class);
		when(toImageOptionsSpec.getCmykPolicy()).thenReturn(CMYKPolicy.EmbedProfile);
		when(toImageOptionsSpec.getColorCompression()).thenReturn(ColorCompression.High);
		when(toImageOptionsSpec.getColorSpace()).thenReturn(ColorSpace.DetermineAutomatically);
		when(toImageOptionsSpec.getFilter()).thenReturn(PNGFilter.None);
		when(toImageOptionsSpec.getFormat()).thenReturn(JPEGFormat.BaselineOptimized);
		when(toImageOptionsSpec.getGrayScaleCompression()).thenReturn(GrayScaleCompression.LZW);
		when(toImageOptionsSpec.getGrayScalePolicy()).thenReturn(GrayScalePolicy.EmbedProfile);
		when(toImageOptionsSpec.getImageConvertFormat()).thenReturn(ImageConvertFormat.JPEG);
		when(toImageOptionsSpec.getImageSizeHeight()).thenReturn("11");
		when(toImageOptionsSpec.getImageSizeWidth()).thenReturn("9");
		when(toImageOptionsSpec.getInterlace()).thenReturn(Interlace.Adam7);
		when(toImageOptionsSpec.getMonochrome()).thenReturn(MonochromeCompression.CCITTG4);
		when(toImageOptionsSpec.getMultiPageTiff()).thenReturn(Boolean.TRUE);
		when(toImageOptionsSpec.getPageRange()).thenReturn("1-1");
		when(toImageOptionsSpec.getResolution()).thenReturn("300");
		when(toImageOptionsSpec.getRgbPolicy()).thenReturn(RGBPolicy.EmbedProfile);
		when(toImageOptionsSpec.getRowsPerStrip()).thenReturn(1);
		when(toImageOptionsSpec.getTileSize()).thenReturn(256);
		when(toImageOptionsSpec.isIncludeComments()).thenReturn(Boolean.TRUE);
		when(toImageOptionsSpec.isUseLegacyImageSizeBehavior()).thenReturn(Boolean.TRUE);

		when(mockPayloadBuilder.add(eq(PDF_PARAM), same(DUMMY_PDF), eq(ContentType.APPLICATION_PDF))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(CMYK_POLICY_PARAM), eq(CMYKPolicy.EmbedProfile))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(COLOR_COMPRESSION_PARAM), eq(ColorCompression.High))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(COLOR_SPACE_PARAM), eq(ColorSpace.DetermineAutomatically))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(PNG_FILTER_PARAM), eq(PNGFilter.None))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(JPEG_FORMAT_PARAM), eq(JPEGFormat.BaselineOptimized))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(GRAY_SCALE_COMPRESSION_PARAM), eq(GrayScaleCompression.LZW))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(GRAY_SCALE_POLICY_PARAM), eq(GrayScalePolicy.EmbedProfile))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(IMAGE_CONVERT_FORMAT_PARAM), eq(ImageConvertFormat.JPEG))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq(IMAGE_SIZE_HEIGHT_PARAM), eq("11"))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq(IMAGE_SIZE_WIDTH_PARAM), eq("9"))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(INTERLACE_PARAM), eq(Interlace.Adam7))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(MONOCHROME_COMPRESSION_PARAM), eq(MonochromeCompression.CCITTG4))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(MULTI_PAGE_TIFF_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq(IMAGE_PAGE_RANGE_PARAM), eq("1-1"))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq(RESOLUTION_PARAM), eq("300"))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(RGB_POLICY_PARAM), eq(RGBPolicy.EmbedProfile))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(ROWS_PER_STRIP_PARAM), eq(1))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(TILE_SIZE_PARAM), eq(256))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(IMAGE_INCLUDE_COMMENTS_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(USE_LEGACY_IMAGE_SIZE_BEHAVIOR_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);

		
		List<Document> imageResult;
		imageResult = underTest.toImage(DUMMY_PDF, toImageOptionsSpec);
		
		// Make sure the correct URL is called.
//		assertAll(
//				()->assertThat("Expected target url contains '" + expectedPrefix + "'", machineName.getValue(), containsString(expectedPrefix)),
//				()->assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME)),
//				()->assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT))),
//				()->assertThat("Expected target url contains 'ToImage'", path.getValue(), containsString("ToImage"))
//		);
		
		// Make sure the response is correct.
		assertArrayEquals(responseData, imageResult.get(0).getInputStream().readAllBytes());
		assertEquals(ContentType.IMAGE_JPEG.contentType(), imageResult.get(0).getContentType());
	}

	@Test
	void testToImage_RestClientException() throws Exception {
		// Mock just enough to get to get to the point of the exception
		var cause = new RestClientException("cause exception");
		var toImageOptionsSpec = Mockito.mock(ToImageOptionsSpec.class);
		var underTest = createAdapter(mockClientFactory);
		when(toImageOptionsSpec.getImageConvertFormat()).thenReturn(ImageConvertFormat.TIFF);

		var ex = mockForException(cause, ()->underTest.toImage(DUMMY_PDF, toImageOptionsSpec));
		
		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("Error while POSTing to server"),
							 ExceptionMatchers.hasCause(cause)
							));
	}
	
	@Test
	void testToImage_IOException() throws Exception {
		// Mock just enough to get to get to the point of the exception
		var cause = new IOException("cause exception");
		var toImageOptionsSpec = Mockito.mock(ToImageOptionsSpec.class);
		var underTest = createAdapter(mockClientFactory);
		when(toImageOptionsSpec.getImageConvertFormat()).thenReturn(ImageConvertFormat.PNG);
		
		var ex = mockForException(cause, ()->underTest.toImage(DUMMY_PDF, toImageOptionsSpec));

		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("I/O Error while securing document"),
				 			 ExceptionMatchers.hasCause(cause)
						     ));
	}
	
	@Test
	void testToPS_HappyPath() throws Exception {
		byte[] responseData = "response Document Data".getBytes();
		setupMocks(setupMockResponse(responseData, ContentType.APPLICATION_PS));
		
		RestServicesConvertPdfServiceAdapter underTest = createAdapter(mockClientFactory);

		ToPSOptionsSpec toPSOptionsSpec = Mockito.mock(ToPSOptionsSpec.class);
		when(toPSOptionsSpec.getColor()).thenReturn(Color.composite);
		when(toPSOptionsSpec.getFontInclusion()).thenReturn(FontInclusion.embeddedFonts);
		when(toPSOptionsSpec.getLineWeight()).thenReturn(LineWeight.point125);
		when(toPSOptionsSpec.getPageRange()).thenReturn("1-1");
		when(toPSOptionsSpec.getPageSize()).thenReturn(PageSize.Custom);
		when(toPSOptionsSpec.getPageSizeHeight()).thenReturn("11");
		when(toPSOptionsSpec.getPageSizeWidth()).thenReturn("9");
		when(toPSOptionsSpec.getPsLevel()).thenReturn(PSLevel.LEVEL_3);
		when(toPSOptionsSpec.getStyle()).thenReturn(Style.Illustrator);
		when(toPSOptionsSpec.isAllowBinaryContent()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isBleedMarks()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isColorBars()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isConvertTrueTypeToType1()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isEmitCIDFontType2()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isEmitPSFormObjects()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isExpandToFit()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isIncludeComments()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isLegacyToSimplePSFlag()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isPageInformation()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isRegistrationMarks()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isReverse()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isRotateAndCenter()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isShrinkToFit()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isTrimMarks()).thenReturn(Boolean.TRUE);
		when(toPSOptionsSpec.isUseMaxJPEGImageResolution()).thenReturn(Boolean.TRUE);
	
		when(mockPayloadBuilder.add(eq(PDF_PARAM), eq(DUMMY_PDF), eq(ContentType.APPLICATION_PDF))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(COLOR_PARAM), eq(Color.composite))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(FONT_INCLUSION_PARAM), eq(FontInclusion.embeddedFonts))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(LINE_WEIGHT_PARAM), eq(LineWeight.point125))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq(PS_PAGE_RANGE_PARAM), eq("1-1"))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(PAGE_SIZE_PARAM), eq(PageSize.Custom))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq(PAGE_SIZE_HEIGHT_PARAM), eq("11"))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq(PAGE_SIZE_WIDTH_PARAM), eq("9"))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(PS_LEVEL_PARAM), eq(PSLevel.LEVEL_3))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(STYLE_PARAM), eq(Style.Illustrator))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(ALLOW_BINARY_CONTENT_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(BLEED_MARKS_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(COLOR_BARS_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(CONVERT_TRUE_TYPE_TO_TYPE1_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(EMIT_CID_FONT_TYPE2_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(EMIT_PS_FORM_OBJECTS_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(EXPAND_TO_FIT_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(PS_INCLUDE_COMMENTS_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(LEGACY_TO_SIMPLE_PS_FLAG_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(PAGE_INFORMATION_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(REGISTRATION_MARKS_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(REVERSE_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(ROTATE_AND_CENTER_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(SHRINK_TO_FIT_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(TRIM_MARKS_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq(USE_MAX_JPEG_IMAGE_RESOLUTION_PARAM), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);

		Document psResult;
		psResult = underTest.toPS(DUMMY_PDF, toPSOptionsSpec);
		
		// Make sure the correct URL is called.
//		final String expectedPrefix = useSSL ? "https://" : "http://";
//		assertAll(
//				()->assertThat("Expected target url contains '" + expectedPrefix + "'", machineName.getValue(), containsString(expectedPrefix)),
//				()->assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME)),
//				()->assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT))),
//				()->assertThat("Expected target url contains 'ToPS'", path.getValue(), containsString("ToPS"))
//		);
		
		// Make sure the response is correct.
		assertArrayEquals(responseData, psResult.getInputStream().readAllBytes());
		assertEquals(ContentType.APPLICATION_PS.contentType(), psResult.getContentType());
	}
	
	@Test
	void testToPS_RestClientException() throws Exception {
		// Mock just enough to get to get to the point of the exception
		var cause = new RestClientException("cause exception");
		var toPSOptionsSpec = Mockito.mock(ToPSOptionsSpec.class);
		var underTest = createAdapter(mockClientFactory);
		
		var ex = mockForException(cause, ()->underTest.toPS(DUMMY_PDF, toPSOptionsSpec));
		
		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("Error while POSTing to server"),
							 ExceptionMatchers.hasCause(cause)
							));		
	}

	@Test
	void testToPS_IOException() throws Exception {
		// Mock just enough to get to get to the point of the exception
		var cause = new IOException("cause exception");
		var toPSOptionsSpec = Mockito.mock(ToPSOptionsSpec.class);
		var underTest = createAdapter(mockClientFactory);
		
		var ex = mockForException(cause, ()->underTest.toPS(DUMMY_PDF, toPSOptionsSpec));
		
		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("I/O Error while securing document"),
	 			 ExceptionMatchers.hasCause(cause)
			     ));
	}

	private <T extends Exception> ConvertPdfServiceException mockForException(T exception, Executable test) throws Exception {
		
		Builder mockPayloadBuilder2 = Mockito.mock(Builder.class, Answers.RETURNS_SELF);
		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder2);
		when(mockPayloadBuilder2.build()).thenReturn(mockPayload);
		
		if (exception instanceof IOException) {
			when(mockResponse.contentType()).thenReturn(ContentType.APPLICATION_PDF);
			when(mockResponse.data()).thenReturn(new ByteArrayInputStream("Dummy response".getBytes()));
			when(mockPayload.postToServer(any())).thenReturn(Optional.of(mockResponse));
			Mockito.doThrow(exception).when(mockPayload).close();
		} else {
			when(mockPayload.postToServer(any())).thenThrow(exception);
		}
		return assertThrows(ConvertPdfServiceException.class, test);
	}
	
	private static RestServicesConvertPdfServiceAdapter createAdapter(TriFunction<AemConfig, String, Supplier<String>, RestClient> clientFactory) {
		return RestServicesConvertPdfServiceAdapter.builder(clientFactory)
											  .machineName(TEST_MACHINE_NAME)
											  .port(TEST_MACHINE_PORT)
											  .basicAuthentication("username", "password")
											  .useSsl(true)
											  .aemServerType(AemServerType.StandardType.JEE)
											  .build();
	}

	private void setupMocks(Optional<Response> mockedResponse) throws RestClientException {
		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.build()).thenReturn(mockPayload);
		when(mockPayload.postToServer(acceptableContentType.capture())).thenReturn(mockedResponse);
	}

	private Optional<Response> setupMockResponse(byte[] responseData, ContentType expectedContentType) {
		when(mockResponse.contentType()).thenReturn(expectedContentType);
		when(mockResponse.data()).thenReturn(new ByteArrayInputStream(responseData));
		return Optional.of(mockResponse);
	}
}
