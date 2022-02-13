package com._4point.aem.fluentforms.api.convertPdf;

import com.adobe.fd.cpdf.api.enumeration.Color;
import com.adobe.fd.cpdf.api.enumeration.FontInclusion;
import com.adobe.fd.cpdf.api.enumeration.LineWeight;
import com.adobe.fd.cpdf.api.enumeration.PageSize;
import com.adobe.fd.cpdf.api.enumeration.PSLevel;
import com.adobe.fd.cpdf.api.enumeration.Style;

public interface ToPSOptionsSpec extends ToPSOptionsSpecSetter {

	Color getColor();
	
	FontInclusion getFontInclusion();
	
	LineWeight getLineWeight();
	
	String getPageRange();
	
	PageSize getPageSize();
	
	String getPageSizeHeight();
	
	String getPageSizeWidth();
	
	PSLevel getPsLevel();
	
	Style getStyle();
	
	Boolean isAllowBinaryContent();
	
	Boolean isBleedMarks();
	
	Boolean isColorBars();
	
	Boolean isConvertTrueTypeToType1();
	
	Boolean isEmitCIDFontType2();
	
	Boolean isEmitPSFormObjects();
	
	Boolean isExpandToFit();
	
	Boolean isIncludeComments();
	
	Boolean isLegacyToSimplePSFlag();
	
	Boolean isPageInformation();
	
	Boolean isRegistrationMarks();
	
	Boolean isReverse();
	
	Boolean isRotateAndCenter();
	
	Boolean isShrinkToFit();
	
	Boolean isTrimMarks();
	
	Boolean isUseMaxJPEGImageResolution();
}
