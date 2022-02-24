package com._4point.aem.docservices.rest_services.client.convertPdf;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.client.convertPdf.RestServicesConvertPdfServiceAdapter.ConvertPdfServiceBuilder;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpec;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
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

	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

//	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final MediaType APPLICATION_JPEG = new MediaType("image", "jpeg");
//	private static final MediaType APPLICATION_JPEG2K = new MediaType("image", "jp2");
//	private static final MediaType APPLICATION_PNG = new MediaType("image", "png");
//	private static final MediaType APPLICATION_TIFF = new MediaType("image", "tiff");
	private static final MediaType APPLICATION_PS = new MediaType("application", "postscript");

	@Mock(answer = Answers.RETURNS_SELF) Client client;	// answers used to mock Client's fluent interface. 
	@Mock WebTarget target;
	@Mock Response response;
	@Mock Builder builder;
	@Mock StatusType statusType;
	
	@Captor ArgumentCaptor<String> machineName;
	@Captor ArgumentCaptor<String> path;
	@SuppressWarnings("rawtypes")
	@Captor ArgumentCaptor<Entity> entity;
	@Captor ArgumentCaptor<String> correlationId;

	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testToImage_NullArguments() throws Exception {
		RestServicesConvertPdfServiceAdapter underTest = RestServicesConvertPdfServiceAdapter.builder().build();
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.toImage((Document)null, null));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("inPdfDoc"));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("cannot be null"));
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.toImage(DUMMY_PDF, null));
		assertThat(ex3.getMessage(), containsStringIgnoringCase("toImageOptionsSpec"));
		assertThat(ex3.getMessage(), containsStringIgnoringCase("cannot be null"));
	}

	@Test
	void testToPS_NullArguments() throws Exception {
		RestServicesConvertPdfServiceAdapter underTest = RestServicesConvertPdfServiceAdapter.builder().build();
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.toPS((Document)null, null));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("inPdfDoc"));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("cannot be null"));
//		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.toPS(DUMMY_PDF, null));
//		assertThat(ex3.getMessage(), containsStringIgnoringCase("toPSOptionsSpec"));
//		assertThat(ex3.getMessage(), containsStringIgnoringCase("cannot be null"));
	}

	private void setupRestClientMocks(Document responseData, MediaType mediaType) throws IOException { 
		// TODO: Change this based on https://maciejwalkowiak.com/mocking-fluent-interfaces/
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(mediaType)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(responseData.getInlineData()));
		when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn(mediaType.toString());
	}

	private enum ToImageHappyPaths { SSL, NO_SSL }
	
	@Disabled //ParameterizedTest
	@EnumSource(ToImageHappyPaths.class)	
	void testToImage_HappyPath_MaxOptions(ToImageHappyPaths codePath) throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());
		setupRestClientMocks(responseData, MediaType.MULTIPART_FORM_DATA_TYPE);
		
		boolean useSSL = false;
		boolean useCorrelationId = false;
		if (codePath == ToImageHappyPaths.SSL) {
			useSSL = true;
			useCorrelationId = true;
			when(builder.header(eq(CORRELATION_ID_HTTP_HDR), correlationId.capture())).thenReturn(builder);
		} else {
			useSSL = false;
			useCorrelationId = false;
		}
		
		ConvertPdfServiceBuilder adapterBuilder = RestServicesConvertPdfServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication("username", "password")
				.useSsl(useSSL)
				.aemServerType(AemServerType.StandardType.JEE)
				.clientFactory(()->client);

		if (useCorrelationId) {
			adapterBuilder.correlationId(()->CORRELATION_ID);
		}

		RestServicesConvertPdfServiceAdapter underTest = adapterBuilder.build();
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
		
		List<Document> imageResult;
		imageResult = underTest.toImage(DUMMY_PDF, toImageOptionsSpec);
		
		// Make sure the correct URL is called.
		final String expectedPrefix = useSSL ? "https://" : "http://";
		assertAll(
				()->assertThat("Expected target url contains '" + expectedPrefix + "'", machineName.getValue(), containsString(expectedPrefix)),
				()->assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME)),
				()->assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT))),
				()->assertThat("Expected target url contains 'ToImage'", path.getValue(), containsString("ToImage"))
		);
		
		if (useCorrelationId) {
			assertEquals(CORRELATION_ID, correlationId.getValue());
		}
		
		// Make sure the response is correct.
		assertArrayEquals(responseData.getInlineData(), imageResult.get(0).getInlineData());
		assertEquals(APPLICATION_JPEG, MediaType.valueOf(imageResult.get(0).getContentType()));
	}
	
	@Disabled //ParameterizedTest
	@EnumSource(ToImageHappyPaths.class)	
	void testToImage_HappyPath_MinOptions(ToImageHappyPaths codePath) throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());
		setupRestClientMocks(responseData, MediaType.MULTIPART_FORM_DATA_TYPE);
		
		boolean useSSL = false;
		boolean useCorrelationId = false;
		if (codePath == ToImageHappyPaths.SSL) {
			useSSL = true;
			useCorrelationId = true;
			when(builder.header(eq(CORRELATION_ID_HTTP_HDR), correlationId.capture())).thenReturn(builder);
		} else {
			useSSL = false;
			useCorrelationId = false;
		}
		
		ConvertPdfServiceBuilder adapterBuilder = RestServicesConvertPdfServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication("username", "password")
				.useSsl(useSSL)
				.aemServerType(AemServerType.StandardType.JEE)
				.clientFactory(()->client);

		if (useCorrelationId) {
			adapterBuilder.correlationId(()->CORRELATION_ID);
		}

		RestServicesConvertPdfServiceAdapter underTest = adapterBuilder.build();
		ToImageOptionsSpec toImageOptionsSpec = Mockito.mock(ToImageOptionsSpec.class);
		when(toImageOptionsSpec.getImageConvertFormat()).thenReturn(ImageConvertFormat.JPEG);
		
		List<Document> imageResult;
		imageResult = underTest.toImage(DUMMY_PDF, toImageOptionsSpec);
		
		// Make sure the correct URL is called.
		final String expectedPrefix = useSSL ? "https://" : "http://";
		assertAll(
				()->assertThat("Expected target url contains '" + expectedPrefix + "'", machineName.getValue(), containsString(expectedPrefix)),
				()->assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME)),
				()->assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT))),
				()->assertThat("Expected target url contains 'ToImage'", path.getValue(), containsString("ToImage"))
		);
		
		if (useCorrelationId) {
			assertEquals(CORRELATION_ID, correlationId.getValue());
		}
		
		// Make sure the response is correct.
		assertArrayEquals(responseData.getInlineData(), imageResult.get(0).getInlineData());
		assertEquals(APPLICATION_JPEG, MediaType.valueOf(imageResult.get(0).getContentType()));
	}
	
	private enum ToPSHappyPaths { 
		SSL_EmptyOptions(true, true),
		NoSSL_EmptyOptions(false, true),
		SSL_Options(true, false),
		NoSSL_Options(false, false);
		
		private final boolean ssl;
		private final boolean emptyOptions;
		
		private ToPSHappyPaths(boolean ssl, boolean emptyOptions) {
			this.ssl = ssl;
			this.emptyOptions = emptyOptions;
		}

		private boolean isSsl() {
			return ssl;
		}
		
		private boolean isEmptyOptions() {
			return emptyOptions;
		}
	}
	
	@ParameterizedTest
	@EnumSource(ToPSHappyPaths.class)
	void testToPS_HappyPath(ToPSHappyPaths codePath) throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());
		setupRestClientMocks(responseData, APPLICATION_PS);
		
		boolean useSSL = false;
		boolean useCorrelationId = false;
		if (codePath.isSsl()) {
			useSSL = true;
			useCorrelationId = true;
			when(builder.header(eq(CORRELATION_ID_HTTP_HDR), correlationId.capture())).thenReturn(builder);
		} else {
			useSSL = false;
			useCorrelationId = false;
		}
		
		ConvertPdfServiceBuilder adapterBuilder = RestServicesConvertPdfServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication("username", "password")
				.useSsl(useSSL)
				.aemServerType(AemServerType.StandardType.JEE)
				.clientFactory(()->client);

		if (useCorrelationId) {
			adapterBuilder.correlationId(()->CORRELATION_ID);
		}

		RestServicesConvertPdfServiceAdapter underTest = adapterBuilder.build();
		ToPSOptionsSpec toPSOptionsSpec = Mockito.mock(ToPSOptionsSpec.class);
		if (!codePath.isEmptyOptions()) {
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
		}
		
		Document psResult;
		psResult = underTest.toPS(DUMMY_PDF, toPSOptionsSpec);
		
		// Make sure the correct URL is called.
		final String expectedPrefix = useSSL ? "https://" : "http://";
		assertAll(
				()->assertThat("Expected target url contains '" + expectedPrefix + "'", machineName.getValue(), containsString(expectedPrefix)),
				()->assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME)),
				()->assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT))),
				()->assertThat("Expected target url contains 'ToPS'", path.getValue(), containsString("ToPS"))
		);
		
		if (useCorrelationId) {
			assertEquals(CORRELATION_ID, correlationId.getValue());
		}
		
		// Make sure the response is correct.
		assertArrayEquals(responseData.getInlineData(), psResult.getInlineData());
		assertEquals(APPLICATION_PS, MediaType.valueOf(psResult.getContentType()));
	}
}
