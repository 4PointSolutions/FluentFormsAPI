package com._4point.aem.fluentforms.api.convertPdf;

import com.adobe.fd.cpdf.api.enumeration.Color;
import com.adobe.fd.cpdf.api.enumeration.FontInclusion;
import com.adobe.fd.cpdf.api.enumeration.LineWeight;
import com.adobe.fd.cpdf.api.enumeration.PageSize;
import com.adobe.fd.cpdf.api.enumeration.PSLevel;
import com.adobe.fd.cpdf.api.enumeration.Style;

public interface ToPSOptionsSpecSetter {

	ToPSOptionsSpecSetter setAllowedBinaryContent(boolean allowBinaryContent);
	
	ToPSOptionsSpecSetter setBleedMarks(boolean bleedMarks);
	
	ToPSOptionsSpecSetter setColor(Color color);
	
	ToPSOptionsSpecSetter setColorBars(boolean colorBars);
	
	ToPSOptionsSpecSetter setConvertTrueTypeToType1(boolean convertTrueTypeToType1);
	
	ToPSOptionsSpecSetter setEmitCIDFontType2(boolean emitCIDFontType2);
	
	ToPSOptionsSpecSetter setEmitPSFormObjects(boolean emitPSFormObjects);
	
	ToPSOptionsSpecSetter setExpandToFit(boolean expandToFit);
	
	ToPSOptionsSpecSetter setFontInclusion(FontInclusion fontInclusion);
	
	ToPSOptionsSpecSetter setIncludeComments(boolean includeComments);
	
	ToPSOptionsSpecSetter setLegacyToSimplePSFlag(boolean legacyToSimplePSFlag);
	
	ToPSOptionsSpecSetter setLineWeight(LineWeight lineWeight);
	
	ToPSOptionsSpecSetter setPageInformation(boolean pageInformation);
	
	ToPSOptionsSpecSetter setPageRange(String pageRange);
	
	ToPSOptionsSpecSetter setPageSize(PageSize pageSize);
	
	ToPSOptionsSpecSetter setPageSizeHeight(String pageSizeHeight);
	
	ToPSOptionsSpecSetter setPageSizeWidth(String pageSizeWidth);
	
	ToPSOptionsSpecSetter setPsLevel(PSLevel psLevel);
	
	ToPSOptionsSpecSetter setRegistrationMarks(boolean registrationMarks);
	
	ToPSOptionsSpecSetter setReverse(boolean reverse);
	
	ToPSOptionsSpecSetter setRotateAndCenter(boolean rotateAndCenter);
	
	ToPSOptionsSpecSetter setShrinkToFit(boolean shrinkToFit);
	
	ToPSOptionsSpecSetter setStyle(Style style);
	
	ToPSOptionsSpecSetter setTrimMarks(boolean trimMarks);
	
	ToPSOptionsSpecSetter setUseMaxJPEGImageResolution(boolean useMaxJPEGImageResolution);
}
