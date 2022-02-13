package com._4point.aem.fluentforms.impl.convertPdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

//import com.adobe.fd.cpdf.api.enumeration.Color;
import com.adobe.fd.cpdf.api.enumeration.FontInclusion;
import com.adobe.fd.cpdf.api.enumeration.LineWeight;
import com.adobe.fd.cpdf.api.enumeration.PSLevel;
import com.adobe.fd.cpdf.api.enumeration.PageSize;
import com.adobe.fd.cpdf.api.enumeration.Style;

public class ToPSOptionsSpecImplTest {

	private final static com.adobe.fd.cpdf.api.ToPSOptionsSpec emptyToPSOptionsSpec = new com.adobe.fd.cpdf.api.ToPSOptionsSpec();
	private final ToPSOptionsSpecImpl underTest = new ToPSOptionsSpecImpl();

	@Test
	@DisplayName("Make sure that if nothing was initialized, then the resulting options are the same as an empty options object.")
	void testAdobeToPSOptionsSpec_NoChanges() {
		assertEmpty(underTest);
	}
	
	private static void assertEmpty(ToPSOptionsSpecImpl toPSOptionsSpecImpl) {
		com.adobe.fd.cpdf.api.ToPSOptionsSpec adobeToPSOptionsSpec = AdobeConvertPdfServiceAdapter.toPSOptionsSpec(toPSOptionsSpecImpl);
		assertEquals(emptyToPSOptionsSpec.getColor(), adobeToPSOptionsSpec.getColor());
		assertEquals(emptyToPSOptionsSpec.getFontInclusion(), adobeToPSOptionsSpec.getFontInclusion());
		assertEquals(emptyToPSOptionsSpec.getLineWeight(), adobeToPSOptionsSpec.getLineWeight());
		assertEquals(emptyToPSOptionsSpec.getPageRange(), adobeToPSOptionsSpec.getPageRange());
		assertEquals(emptyToPSOptionsSpec.getPageSize(), adobeToPSOptionsSpec.getPageSize());
		assertEquals(emptyToPSOptionsSpec.getPageSizeHeight(), adobeToPSOptionsSpec.getPageSizeHeight());
		assertEquals(emptyToPSOptionsSpec.getPageSizeWidth(), adobeToPSOptionsSpec.getPageSizeWidth());
		assertEquals(emptyToPSOptionsSpec.getPsLevel(), adobeToPSOptionsSpec.getPsLevel());
		assertEquals(emptyToPSOptionsSpec.getStyle(), adobeToPSOptionsSpec.getStyle());
		assertEquals(emptyToPSOptionsSpec.isAllowBinaryContent(), adobeToPSOptionsSpec.isAllowBinaryContent());
		assertEquals(emptyToPSOptionsSpec.isBleedMarks(), adobeToPSOptionsSpec.isBleedMarks());
		assertEquals(emptyToPSOptionsSpec.isColorBars(), adobeToPSOptionsSpec.isColorBars());
		assertEquals(emptyToPSOptionsSpec.isConvertTrueTypeToType1(), adobeToPSOptionsSpec.isConvertTrueTypeToType1());
		assertEquals(emptyToPSOptionsSpec.isEmitCIDFontType2(), adobeToPSOptionsSpec.isEmitCIDFontType2());
		assertEquals(emptyToPSOptionsSpec.isEmitPSFormObjects(), adobeToPSOptionsSpec.isEmitPSFormObjects());
		assertEquals(emptyToPSOptionsSpec.isExpandToFit(), adobeToPSOptionsSpec.isExpandToFit());
		assertEquals(emptyToPSOptionsSpec.isIncludeComments(), adobeToPSOptionsSpec.isIncludeComments());
		assertEquals(emptyToPSOptionsSpec.isLegacyToSimplePSFlag(), adobeToPSOptionsSpec.isLegacyToSimplePSFlag());
		assertEquals(emptyToPSOptionsSpec.isPageInformation(), adobeToPSOptionsSpec.isPageInformation());
		assertEquals(emptyToPSOptionsSpec.isRegistrationMarks(), adobeToPSOptionsSpec.isRegistrationMarks());
		assertEquals(emptyToPSOptionsSpec.isReverse(), adobeToPSOptionsSpec.isReverse());
		assertEquals(emptyToPSOptionsSpec.isRotateAndCenter(), adobeToPSOptionsSpec.isRotateAndCenter());
		assertEquals(emptyToPSOptionsSpec.isShrinkToFit(), adobeToPSOptionsSpec.isShrinkToFit());
		assertEquals(emptyToPSOptionsSpec.isTrimMarks(), adobeToPSOptionsSpec.isTrimMarks());
		assertEquals(emptyToPSOptionsSpec.isUseMaxJPEGImageResolution(), adobeToPSOptionsSpec.isUseMaxJPEGImageResolution());
	}
	
	@Test
	@DisplayName("Make sure that if most things were initialized, then the resulting options are the not same as an empty options object.")
	void testAdobeToPSOptionsSpec_AllChanges() {
		underTest.setAllowedBinaryContent(true);
		underTest.setBleedMarks(true);
//		underTest.setColor(Color.composite); // defaults are composite & compositeGray -> both recognized as composite; can't use valueOf(color)
		underTest.setColorBars(true);
		underTest.setConvertTrueTypeToType1(false);
		underTest.setEmitCIDFontType2(true);
		underTest.setEmitPSFormObjects(true);
		underTest.setExpandToFit(true);
		underTest.setFontInclusion(FontInclusion.none); // default is embeddedFonts
		underTest.setIncludeComments(true);
		underTest.setLegacyToSimplePSFlag(true);
		underTest.setLineWeight(LineWeight.point125);
		underTest.setPageInformation(true);
		underTest.setPageRange("1-1");
		underTest.setPageSize(PageSize.Letter);
		underTest.setPageSizeHeight("11in");
		underTest.setPageSizeWidth("8.5in");
		underTest.setPsLevel(PSLevel.LEVEL_3);
		underTest.setRegistrationMarks(true);
		underTest.setReverse(true);
		underTest.setRotateAndCenter(true);
		underTest.setShrinkToFit(true);
		underTest.setStyle(Style.Illustrator);
		underTest.setTrimMarks(true);
		underTest.setUseMaxJPEGImageResolution(false);

		assertNotEmpty(underTest);
	}
	
	private static void assertNotEmpty(ToPSOptionsSpecImpl toPSOptionsSpecImpl) {
		com.adobe.fd.cpdf.api.ToPSOptionsSpec adobeToPSOptionsSpec = AdobeConvertPdfServiceAdapter.toPSOptionsSpec(toPSOptionsSpecImpl);
//		assertNotEquals(emptyToPSOptionsSpec.getColor(), adobeToPSOptionsSpec.getColor()); // fails if color is composite or compositeGray
		assertNotEquals(emptyToPSOptionsSpec.getFontInclusion(), adobeToPSOptionsSpec.getFontInclusion());
		assertNotEquals(emptyToPSOptionsSpec.getLineWeight(), adobeToPSOptionsSpec.getLineWeight());
		assertNotEquals(emptyToPSOptionsSpec.getPageRange(), adobeToPSOptionsSpec.getPageRange());
		assertNotEquals(emptyToPSOptionsSpec.getPageSize(), adobeToPSOptionsSpec.getPageSize());
		assertNotEquals(emptyToPSOptionsSpec.getPageSizeHeight(), adobeToPSOptionsSpec.getPageSizeHeight());
		assertNotEquals(emptyToPSOptionsSpec.getPageSizeWidth(), adobeToPSOptionsSpec.getPageSizeWidth());
		assertNotEquals(emptyToPSOptionsSpec.getPsLevel(), adobeToPSOptionsSpec.getPsLevel());
		assertNotEquals(emptyToPSOptionsSpec.getStyle(), adobeToPSOptionsSpec.getStyle());
		assertNotEquals(emptyToPSOptionsSpec.isAllowBinaryContent(), adobeToPSOptionsSpec.isAllowBinaryContent());
		assertNotEquals(emptyToPSOptionsSpec.isBleedMarks(), adobeToPSOptionsSpec.isBleedMarks());
		assertNotEquals(emptyToPSOptionsSpec.isColorBars(), adobeToPSOptionsSpec.isColorBars());
		assertNotEquals(emptyToPSOptionsSpec.isConvertTrueTypeToType1(), adobeToPSOptionsSpec.isConvertTrueTypeToType1());
		assertNotEquals(emptyToPSOptionsSpec.isEmitCIDFontType2(), adobeToPSOptionsSpec.isEmitCIDFontType2());
		assertNotEquals(emptyToPSOptionsSpec.isEmitPSFormObjects(), adobeToPSOptionsSpec.isEmitPSFormObjects());
		assertNotEquals(emptyToPSOptionsSpec.isExpandToFit(), adobeToPSOptionsSpec.isExpandToFit());
		assertNotEquals(emptyToPSOptionsSpec.isIncludeComments(), adobeToPSOptionsSpec.isIncludeComments());
		assertNotEquals(emptyToPSOptionsSpec.isLegacyToSimplePSFlag(), adobeToPSOptionsSpec.isLegacyToSimplePSFlag());
		assertNotEquals(emptyToPSOptionsSpec.isPageInformation(), adobeToPSOptionsSpec.isPageInformation());
		assertNotEquals(emptyToPSOptionsSpec.isRegistrationMarks(), adobeToPSOptionsSpec.isRegistrationMarks());
		assertNotEquals(emptyToPSOptionsSpec.isReverse(), adobeToPSOptionsSpec.isReverse());
		assertNotEquals(emptyToPSOptionsSpec.isRotateAndCenter(), adobeToPSOptionsSpec.isRotateAndCenter());
		assertNotEquals(emptyToPSOptionsSpec.isShrinkToFit(), adobeToPSOptionsSpec.isShrinkToFit());
		assertNotEquals(emptyToPSOptionsSpec.isTrimMarks(), adobeToPSOptionsSpec.isTrimMarks());
		assertNotEquals(emptyToPSOptionsSpec.isUseMaxJPEGImageResolution(), adobeToPSOptionsSpec.isUseMaxJPEGImageResolution());
	}
}
