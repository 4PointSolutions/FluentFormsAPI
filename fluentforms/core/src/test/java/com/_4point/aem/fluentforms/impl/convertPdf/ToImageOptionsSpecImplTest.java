package com._4point.aem.fluentforms.impl.convertPdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

public class ToImageOptionsSpecImplTest {

	private final static com.adobe.fd.cpdf.api.ToImageOptionsSpec emptyToImageOptionsSpec = new com.adobe.fd.cpdf.api.ToImageOptionsSpec();
	private final ToImageOptionsSpecImpl underTest = new ToImageOptionsSpecImpl();

	@Test
	@DisplayName("Make sure that if nothing was initialized, then the resulting options are the same as an empty options object.")
	void testAdobeToImageOptionsSpec_NoChanges() {
		assertEmpty(underTest);
	}

	private static void assertEmpty(ToImageOptionsSpecImpl toImageOptionsSpec) {
		com.adobe.fd.cpdf.api.ToImageOptionsSpec adobeToImageOptionsSpec = AdobeConvertPdfServiceAdapter.toImageOptionsSpec(toImageOptionsSpec);
		assertEquals(emptyToImageOptionsSpec.getCmykPolicy(), adobeToImageOptionsSpec.getCmykPolicy());
		assertEquals(emptyToImageOptionsSpec.getColorCompression(), adobeToImageOptionsSpec.getColorCompression());
		assertEquals(emptyToImageOptionsSpec.getColorSpace(), adobeToImageOptionsSpec.getColorSpace());
		assertEquals(emptyToImageOptionsSpec.getFilter(), adobeToImageOptionsSpec.getFilter());
		assertEquals(emptyToImageOptionsSpec.getFormat(), adobeToImageOptionsSpec.getFormat());
		assertEquals(emptyToImageOptionsSpec.getGrayScaleCompression(), adobeToImageOptionsSpec.getGrayScaleCompression());
		assertEquals(emptyToImageOptionsSpec.getGrayScalePolicy(), adobeToImageOptionsSpec.getGrayScalePolicy());
		assertEquals(emptyToImageOptionsSpec.getImageConvertFormat(), adobeToImageOptionsSpec.getImageConvertFormat());
		assertEquals(emptyToImageOptionsSpec.getImageSizeHeight(), adobeToImageOptionsSpec.getImageSizeHeight());
		assertEquals(emptyToImageOptionsSpec.getImageSizeWidth(), adobeToImageOptionsSpec.getImageSizeWidth());
		assertEquals(emptyToImageOptionsSpec.getInterlace(), adobeToImageOptionsSpec.getInterlace());
		assertEquals(emptyToImageOptionsSpec.getMonochrome(), adobeToImageOptionsSpec.getMonochrome());
		assertEquals(emptyToImageOptionsSpec.getMultiPageTiff(), adobeToImageOptionsSpec.getMultiPageTiff());
		assertEquals(emptyToImageOptionsSpec.getPageRange(), adobeToImageOptionsSpec.getPageRange());
		assertEquals(emptyToImageOptionsSpec.getResolution(), adobeToImageOptionsSpec.getResolution());
		assertEquals(emptyToImageOptionsSpec.getRgbPolicy(), adobeToImageOptionsSpec.getRgbPolicy());
		assertEquals(emptyToImageOptionsSpec.getRowsPerStrip(), adobeToImageOptionsSpec.getRowsPerStrip());
		assertEquals(emptyToImageOptionsSpec.getTileSize(), adobeToImageOptionsSpec.getTileSize());
		assertEquals(emptyToImageOptionsSpec.isIncludeComments(), adobeToImageOptionsSpec.isIncludeComments());
		assertEquals(emptyToImageOptionsSpec.isUseLegacyImageSizeBehavior(), adobeToImageOptionsSpec.isUseLegacyImageSizeBehavior());
	}
	
	@Test
	@DisplayName("Make sure that if most things were initialized, then the resulting options are the not same as an empty options object.")
	void testAdobeToImageOptionsSpec_AllChanges() {
		underTest.setCmykPolicy(CMYKPolicy.EmbedProfile); // default is Off
		underTest.setColorCompression(ColorCompression.None);
		underTest.setColorSpace(ColorSpace.GrayScale);
		underTest.setFilter(PNGFilter.None);
		underTest.setFormat(JPEGFormat.BaselineOptimized); // default is BaselineStandard
		underTest.setGrayScaleCompression(GrayScaleCompression.Lossless);
		underTest.setGrayScalePolicy(GrayScalePolicy.EmbedProfile); // default is Off
		underTest.setImageConvertFormat(ImageConvertFormat.JPEG);
		underTest.setImageSizeHeight("11in");
		underTest.setImageSizeWidth("8.5in");
		underTest.setIncludeComments(false);
		underTest.setInterlace(Interlace.Adam7); // default is None
		underTest.setMonochrome(MonochromeCompression.LZW);
		underTest.setMultiPageTiff(false);
		underTest.setPageRange("1-1");
		underTest.setResolution("300");
		underTest.setRgbPolicy(RGBPolicy.Off);
		underTest.setRowsPerStrip(1);
		underTest.setTileSize(128); // default is 256
		underTest.setUseLegacyImageSizeBehavior(false);

		assertNotEmpty(underTest);
	}
	
	private static void assertNotEmpty(ToImageOptionsSpecImpl toImageOptionsSpec) {
		com.adobe.fd.cpdf.api.ToImageOptionsSpec adobeToImageOptionsSpec = AdobeConvertPdfServiceAdapter.toImageOptionsSpec(toImageOptionsSpec);
		assertNotEquals(emptyToImageOptionsSpec.getCmykPolicy(), adobeToImageOptionsSpec.getCmykPolicy());
		assertNotEquals(emptyToImageOptionsSpec.getColorCompression(), adobeToImageOptionsSpec.getColorCompression());
		assertNotEquals(emptyToImageOptionsSpec.getColorSpace(), adobeToImageOptionsSpec.getColorSpace());
		assertNotEquals(emptyToImageOptionsSpec.getFilter(), adobeToImageOptionsSpec.getFilter());
		assertNotEquals(emptyToImageOptionsSpec.getFormat(), adobeToImageOptionsSpec.getFormat());
		assertNotEquals(emptyToImageOptionsSpec.getGrayScaleCompression(), adobeToImageOptionsSpec.getGrayScaleCompression());
		assertNotEquals(emptyToImageOptionsSpec.getGrayScalePolicy(), adobeToImageOptionsSpec.getGrayScalePolicy());
		assertNotEquals(emptyToImageOptionsSpec.getImageConvertFormat(), adobeToImageOptionsSpec.getImageConvertFormat());
		assertNotEquals(emptyToImageOptionsSpec.getImageSizeHeight(), adobeToImageOptionsSpec.getImageSizeHeight());
		assertNotEquals(emptyToImageOptionsSpec.getImageSizeWidth(), adobeToImageOptionsSpec.getImageSizeWidth());
		assertNotEquals(emptyToImageOptionsSpec.getInterlace(), adobeToImageOptionsSpec.getInterlace());
		assertNotEquals(emptyToImageOptionsSpec.getMonochrome(), adobeToImageOptionsSpec.getMonochrome());
		assertNotEquals(emptyToImageOptionsSpec.getMultiPageTiff(), adobeToImageOptionsSpec.getMultiPageTiff());
		assertNotEquals(emptyToImageOptionsSpec.getPageRange(), adobeToImageOptionsSpec.getPageRange());
		assertNotEquals(emptyToImageOptionsSpec.getResolution(), adobeToImageOptionsSpec.getResolution());
		assertNotEquals(emptyToImageOptionsSpec.getRgbPolicy(), adobeToImageOptionsSpec.getRgbPolicy());
		assertNotEquals(emptyToImageOptionsSpec.getRowsPerStrip(), adobeToImageOptionsSpec.getRowsPerStrip());
		assertNotEquals(emptyToImageOptionsSpec.getTileSize(), adobeToImageOptionsSpec.getTileSize());
		assertNotEquals(emptyToImageOptionsSpec.isIncludeComments(), adobeToImageOptionsSpec.isIncludeComments());
		assertNotEquals(emptyToImageOptionsSpec.isUseLegacyImageSizeBehavior(), adobeToImageOptionsSpec.isUseLegacyImageSizeBehavior());
		
	}
}
