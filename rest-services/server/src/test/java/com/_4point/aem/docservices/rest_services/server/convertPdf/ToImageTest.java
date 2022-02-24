package com._4point.aem.docservices.rest_services.server.convertPdf;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com._4point.aem.docservices.rest_services.server.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.impl.convertPdf.TraditionalConvertPdfService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.convertPdf.MockTraditionalConvertPdfService;
import com._4point.aem.fluentforms.testing.convertPdf.MockTraditionalConvertPdfService.ToImageArgs;
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

import io.wcm.testing.mock.aem.junit5.AemContext;

public class ToImageTest {
	private static final String APPLICATION_JPEG = "image/jpeg";
	private static final String APPLICATION_JPEG2K = "image/jp2"; // image/jpx, image/jpm?
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String APPLICATION_PNG = "image/png";
	private static final String APPLICATION_TIFF = "image/tiff";
	
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
	private static final String INCLUDE_COMMENTS_PARAM = "toImageOptionsSpec.includeComments";
	private static final String INTERLACE_PARAM = "toImageOptionsSpec.interlace";
	private static final String MONOCHROME_COMPRESSION_PARAM = "toImageOptionsSpec.monochrome";
	private static final String MULTI_PAGE_TIFF_PARAM = "toImageOptionsSpec.multiPageTiff";
	private static final String PAGE_RANGE_PARAM = "toImageOptionsSpec.pageRange";
	private static final String RESOLUTION_PARAM = "toImageOptionsSpec.resolution";
	private static final String RGB_POLICY_PARAM = "toImageOptionsSpec.rgbPolicy";
	private static final String ROWS_PER_STRIP_PARAM = "toImageOptionsSpec.rowsPerStrip";
	private static final String TILE_SIZE_PARAM = "toImageOptionsSpec.tileSize";
	private static final String USE_LEGACY_IMAGE_SIZE_BEHAVIOR_PARAM = "toImageOptionsSpec.useLegacyImageSizeBehavior";
	
	private final ToImage underTest =  new ToImage();

	private final AemContext aemContext = new AemContext();

	private MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();

	@BeforeEach
	void setUp() throws Exception {
		// Always use the MockDocumentFactory() in the class that's under test because the Adobe Document object has unresolved dependencies.
		junitx.util.PrivateAccessor.setField(underTest, "docFactory", (DocumentFactory)mockDocumentFactory);
	}

	private enum Image {
		JPEG("JPEG", APPLICATION_JPEG, ImageConvertFormat.JPEG),
		JPEG2000("JPEG2K", APPLICATION_JPEG2K, ImageConvertFormat.JPEG2K),
		PNG("PNG", APPLICATION_PNG, ImageConvertFormat.PNG),
		TIFF("TIFF", APPLICATION_TIFF, ImageConvertFormat.TIFF);
		
		String format;
		String mimeType;
		ImageConvertFormat imageConvertFormat;
		
		Image(String format, String mimeType, ImageConvertFormat imageConvertFormat) {
			this.format = format;
			this.mimeType = mimeType;
			this.imageConvertFormat = imageConvertFormat;
		}

		private String getFormat() {
			return format;
		}

		private String getMimeType() {
			return mimeType;
		}

		private ImageConvertFormat getImageConvertFormat() {
			return imageConvertFormat;
		}
	}
	
	@ParameterizedTest
	@EnumSource
	void testDoPost_HappyPath_MinArgs(Image image) throws ServletException, IOException, NoSuchFieldException {
		byte[] inPdfDoc = Files.readAllBytes(TestUtils.SAMPLE_PDF);
		String resultData = "testDoPost Happy Path Result";
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalConvertPdfService convertPdfMock = mockToImage(resultDataBytes, image.getMimeType());
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.addRequestParameter(PDF_PARAM, inPdfDoc, APPLICATION_PDF);
		request.addRequestParameter(IMAGE_CONVERT_FORMAT_PARAM, image.getFormat());
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(image.getMimeType(), response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		
		ToImageArgs toImageArgs = convertPdfMock.getToImageArgs();
		ToImageOptionsSpec toImageOptionsSpec = toImageArgs.getToImageOptionsSpec();
		assertEquals(image.getImageConvertFormat(), toImageOptionsSpec.getImageConvertFormat());
	}

	@ParameterizedTest
	@EnumSource
	void testDoPost_HappyPath_MaxArgs_Defaults(Image image) throws ServletException, IOException, NoSuchFieldException {
		String resultData = "testDoPost Happy Path Result";
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalConvertPdfService convertPdfMock = mockToImage(resultDataBytes, image.getMimeType());
		
		byte[] inPdfDoc = Files.readAllBytes(TestUtils.SAMPLE_PDF);
		String cmykPolicy = "Off";
		String colorCompression = "None";
		String colorSpace = "GrayScale";
		String pngFilter = "None";
		String jpegFormat = "BaselineStandard";
		String grayScaleCompression = "None";
		String grayScalePolicy = "Off";
		String imageConvertFormat = image.getFormat();
		String imageSizeHeight = "11";
		String imageSizeWidth = "8.5";
		boolean includeComments = true;
		String interlace = "None";
		String monochrome = "None";
		boolean multiPageTiff = true;
		String pageRange = "1-1";
		String resolution = "300";
		String rgbPolicy = "Off";
		int rowsPerStrip = 1;
		int tileSize = 256;
		boolean useLegacyImageSizeBehavior = true;
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.addRequestParameter(PDF_PARAM, inPdfDoc, APPLICATION_PDF);
		request.addRequestParameter(CMYK_POLICY_PARAM, cmykPolicy);
		request.addRequestParameter(COLOR_COMPRESSION_PARAM, colorCompression);
		request.addRequestParameter(COLOR_SPACE_PARAM, colorSpace);
		request.addRequestParameter(PNG_FILTER_PARAM, pngFilter);
		request.addRequestParameter(JPEG_FORMAT_PARAM, jpegFormat);
		request.addRequestParameter(GRAY_SCALE_COMPRESSION_PARAM, grayScaleCompression);
		request.addRequestParameter(GRAY_SCALE_POLICY_PARAM, grayScalePolicy);
		request.addRequestParameter(IMAGE_CONVERT_FORMAT_PARAM, imageConvertFormat);
		request.addRequestParameter(IMAGE_SIZE_HEIGHT_PARAM, imageSizeHeight);
		request.addRequestParameter(IMAGE_SIZE_WIDTH_PARAM, imageSizeWidth);
		request.addRequestParameter(INCLUDE_COMMENTS_PARAM, Boolean.toString(includeComments));
		request.addRequestParameter(INTERLACE_PARAM, interlace);
		request.addRequestParameter(MONOCHROME_COMPRESSION_PARAM, monochrome);
		request.addRequestParameter(MULTI_PAGE_TIFF_PARAM, Boolean.toString(multiPageTiff));
		request.addRequestParameter(PAGE_RANGE_PARAM, pageRange);
		request.addRequestParameter(RESOLUTION_PARAM, resolution);
		request.addRequestParameter(RGB_POLICY_PARAM, rgbPolicy);
		request.addRequestParameter(ROWS_PER_STRIP_PARAM, String.valueOf(rowsPerStrip));
		request.addRequestParameter(TILE_SIZE_PARAM, String.valueOf(tileSize));
		request.addRequestParameter(USE_LEGACY_IMAGE_SIZE_BEHAVIOR_PARAM, Boolean.toString(useLegacyImageSizeBehavior));
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(image.getMimeType(), response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		
		ToImageArgs toImageArgs = convertPdfMock.getToImageArgs();
		ToImageOptionsSpec toImageOptionsSpec = toImageArgs.getToImageOptionsSpec();
		assertAll(
				()->assertEquals(CMYKPolicy.Off, toImageOptionsSpec.getCmykPolicy()),
				()->assertEquals(ColorCompression.None, toImageOptionsSpec.getColorCompression()),
				()->assertEquals(ColorSpace.GrayScale, toImageOptionsSpec.getColorSpace()),
				()->assertEquals(PNGFilter.None, toImageOptionsSpec.getFilter()),
				()->assertEquals(JPEGFormat.BaselineStandard, toImageOptionsSpec.getFormat()),
				()->assertEquals(GrayScaleCompression.None, toImageOptionsSpec.getGrayScaleCompression()),
				()->assertEquals(GrayScalePolicy.Off, toImageOptionsSpec.getGrayScalePolicy()),
				()->assertEquals(image.getImageConvertFormat(), toImageOptionsSpec.getImageConvertFormat()),
				()->assertEquals(imageSizeHeight, toImageOptionsSpec.getImageSizeHeight()),
				()->assertEquals(imageSizeWidth, toImageOptionsSpec.getImageSizeWidth()),
				()->assertTrue(toImageOptionsSpec.isIncludeComments()),
				()->assertEquals(Interlace.None, toImageOptionsSpec.getInterlace()),
				()->assertEquals(MonochromeCompression.None, toImageOptionsSpec.getMonochrome()),
				()->assertTrue(toImageOptionsSpec.getMultiPageTiff()),
				()->assertEquals(pageRange, toImageOptionsSpec.getPageRange()),
				()->assertEquals(resolution, toImageOptionsSpec.getResolution()),
				()->assertEquals(RGBPolicy.Off, toImageOptionsSpec.getRgbPolicy()),
				()->assertEquals(rowsPerStrip, toImageOptionsSpec.getRowsPerStrip()),
				()->assertEquals(tileSize, toImageOptionsSpec.getTileSize()),
				()->assertTrue(toImageOptionsSpec.isUseLegacyImageSizeBehavior())
		);
	}

	@Test
	void testDoPost_HappyPath_MaxArgs_JPEG() throws ServletException, IOException, NoSuchFieldException {
		String resultData = "testDoPost Happy Path Result";
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalConvertPdfService convertPdfMock = mockToImage(resultDataBytes, APPLICATION_JPEG);
		
		byte[] inPdfDoc = Files.readAllBytes(TestUtils.SAMPLE_PDF);
		String cmykPolicy = "EmbedProfile";
		String colorCompression = "Lossless";
		String colorSpace = "CMYK";
		String pngFilter = "None";
		String jpegFormat = "BaselineOptimized";
		String grayScaleCompression = "Lossless";
		String grayScalePolicy = "EmbedProfile";
		String imageConvertFormat = "JPEG";
		String imageSizeHeight = "11";
		String imageSizeWidth = "8.5";
		boolean includeComments = false;
		String interlace = "Adam7";
		String monochrome = "ZIP";
		boolean multiPageTiff = false;
		String pageRange = "1-1";
		String resolution = "300";
		String rgbPolicy = "EmbedProfile";
		int rowsPerStrip = 1;
		int tileSize = 128;
		boolean useLegacyImageSizeBehavior = false;
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.addRequestParameter(PDF_PARAM, inPdfDoc, APPLICATION_PDF);
		request.addRequestParameter(CMYK_POLICY_PARAM, cmykPolicy);
		request.addRequestParameter(COLOR_COMPRESSION_PARAM, colorCompression);
		request.addRequestParameter(COLOR_SPACE_PARAM, colorSpace);
		request.addRequestParameter(PNG_FILTER_PARAM, pngFilter);
		request.addRequestParameter(JPEG_FORMAT_PARAM, jpegFormat);
		request.addRequestParameter(GRAY_SCALE_COMPRESSION_PARAM, grayScaleCompression);
		request.addRequestParameter(GRAY_SCALE_POLICY_PARAM, grayScalePolicy);
		request.addRequestParameter(IMAGE_CONVERT_FORMAT_PARAM, imageConvertFormat);
		request.addRequestParameter(IMAGE_SIZE_HEIGHT_PARAM, imageSizeHeight);
		request.addRequestParameter(IMAGE_SIZE_WIDTH_PARAM, imageSizeWidth);
		request.addRequestParameter(INCLUDE_COMMENTS_PARAM, Boolean.toString(includeComments));
		request.addRequestParameter(INTERLACE_PARAM, interlace);
		request.addRequestParameter(MONOCHROME_COMPRESSION_PARAM, monochrome);
		request.addRequestParameter(MULTI_PAGE_TIFF_PARAM, Boolean.toString(multiPageTiff));
		request.addRequestParameter(PAGE_RANGE_PARAM, pageRange);
		request.addRequestParameter(RESOLUTION_PARAM, resolution);
		request.addRequestParameter(RGB_POLICY_PARAM, rgbPolicy);
		request.addRequestParameter(ROWS_PER_STRIP_PARAM, String.valueOf(rowsPerStrip));
		request.addRequestParameter(TILE_SIZE_PARAM, String.valueOf(tileSize));
		request.addRequestParameter(USE_LEGACY_IMAGE_SIZE_BEHAVIOR_PARAM, Boolean.toString(useLegacyImageSizeBehavior));
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_JPEG, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		
		ToImageArgs toImageArgs = convertPdfMock.getToImageArgs();
		ToImageOptionsSpec toImageOptionsSpec = toImageArgs.getToImageOptionsSpec();
		assertAll(
				()->assertEquals(CMYKPolicy.EmbedProfile, toImageOptionsSpec.getCmykPolicy()),
				()->assertEquals(ColorCompression.Lossless, toImageOptionsSpec.getColorCompression()),
				()->assertEquals(ColorSpace.CMYK, toImageOptionsSpec.getColorSpace()),
				()->assertEquals(PNGFilter.None, toImageOptionsSpec.getFilter()),
				()->assertEquals(JPEGFormat.BaselineOptimized, toImageOptionsSpec.getFormat()),
				()->assertEquals(GrayScaleCompression.Lossless, toImageOptionsSpec.getGrayScaleCompression()),
				()->assertEquals(GrayScalePolicy.EmbedProfile, toImageOptionsSpec.getGrayScalePolicy()),
				()->assertEquals(ImageConvertFormat.JPEG, toImageOptionsSpec.getImageConvertFormat()),
				()->assertEquals(imageSizeHeight, toImageOptionsSpec.getImageSizeHeight()),
				()->assertEquals(imageSizeWidth, toImageOptionsSpec.getImageSizeWidth()),
				()->assertFalse(toImageOptionsSpec.isIncludeComments()),
				()->assertEquals(Interlace.Adam7, toImageOptionsSpec.getInterlace()),
				()->assertEquals(MonochromeCompression.ZIP, toImageOptionsSpec.getMonochrome()),
				()->assertFalse(toImageOptionsSpec.getMultiPageTiff()),
				()->assertEquals(pageRange, toImageOptionsSpec.getPageRange()),
				()->assertEquals(resolution, toImageOptionsSpec.getResolution()),
				()->assertEquals(RGBPolicy.EmbedProfile, toImageOptionsSpec.getRgbPolicy()),
				()->assertEquals(rowsPerStrip, toImageOptionsSpec.getRowsPerStrip()),
				()->assertEquals(tileSize, toImageOptionsSpec.getTileSize()),
				()->assertFalse(toImageOptionsSpec.isUseLegacyImageSizeBehavior())
		);
	}

	public MockTraditionalConvertPdfService mockToImage(byte[] resultDataBytes, String contentType) throws NoSuchFieldException {
		Document convertPdfResult = mockDocumentFactory.create(resultDataBytes);
		convertPdfResult.setContentType(contentType);
		List<Document> convertPdfResultList = Collections.singletonList(convertPdfResult);
		MockTraditionalConvertPdfService convertPdfMock = MockTraditionalConvertPdfService.createDocumentListMock(convertPdfResultList);
		junitx.util.PrivateAccessor.setField(underTest, "convertPdfServiceFactory", (Supplier<TraditionalConvertPdfService>)()->(TraditionalConvertPdfService)convertPdfMock);
		return convertPdfMock;
	}
}
